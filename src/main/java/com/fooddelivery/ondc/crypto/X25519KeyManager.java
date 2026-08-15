package com.fooddelivery.ondc.crypto;

import com.fooddelivery.ondc.config.OndcProperties;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Manages X25519 encryption key operations for ONDC subscription challenge.
 * Computes shared secrets and decrypts AES challenges.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class X25519KeyManager {
    @java.lang.SuppressWarnings("all")

    private final OndcProperties ondcProperties;

    public X25519KeyManager(OndcProperties ondcProperties) {
        this.ondcProperties = ondcProperties;
    }

    /**
     * Computes a shared secret using our X25519 private key and the ONDC public key.
     * This shared secret is used for AES decryption of the subscription challenge.
     *
     * @param ondcPublicKeyB64 the ONDC environment's X25519 public key (Base64/DER)
     * @return 32-byte shared secret
     */
    public byte[] computeSharedSecret(String ondcPublicKeyB64) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(ondcProperties.getCrypto().getEncryptionPrivateKey());
        byte[] publicKeyBytes = Base64.getDecoder().decode(ondcPublicKeyB64);
        // Strip ASN.1 DER header if present (X25519 raw key is 32 bytes)
        if (publicKeyBytes.length > 32) {
            byte[] rawKey = new byte[32];
            System.arraycopy(publicKeyBytes, publicKeyBytes.length - 32, rawKey, 0, 32);
            publicKeyBytes = rawKey;
        }
        if (privateKeyBytes.length > 32) {
            byte[] rawKey = new byte[32];
            System.arraycopy(privateKeyBytes, privateKeyBytes.length - 32, rawKey, 0, 32);
            privateKeyBytes = rawKey;
        }
        X25519PrivateKeyParameters privateKey = new X25519PrivateKeyParameters(privateKeyBytes, 0);
        X25519PublicKeyParameters publicKey = new X25519PublicKeyParameters(publicKeyBytes, 0);
        X25519Agreement agreement = new X25519Agreement();
        agreement.init(privateKey);
        byte[] sharedSecret = new byte[agreement.getAgreementSize()];
        agreement.calculateAgreement(publicKey, sharedSecret, 0);
        return sharedSecret;
    }

    /**
     * Decrypts an AES-encrypted challenge string using the computed shared secret.
     *
     * @param encryptedChallengeB64 Base64-encoded AES-encrypted challenge
     * @param ondcPublicKeyB64      ONDC environment public key
     * @return decrypted challenge string
     */
    public String decryptChallenge(String encryptedChallengeB64, String ondcPublicKeyB64) {
        try {
            byte[] sharedSecret = computeSharedSecret(ondcPublicKeyB64);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedChallengeB64);
            SecretKeySpec keySpec = new SecretKeySpec(sharedSecret, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES challenge decryption failed: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to decrypt ONDC subscription challenge", e);
        }
    }
}
