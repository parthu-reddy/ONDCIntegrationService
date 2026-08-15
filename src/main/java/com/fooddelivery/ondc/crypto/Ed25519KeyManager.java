package com.fooddelivery.ondc.crypto;

import com.fooddelivery.ondc.config.OndcProperties;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Manages Ed25519 signing key operations for ONDC message authentication.
 * Signs outgoing payloads and verifies incoming signatures.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class Ed25519KeyManager {
    @java.lang.SuppressWarnings("all")

    private final OndcProperties ondcProperties;

    public Ed25519KeyManager(OndcProperties ondcProperties) {
        this.ondcProperties = ondcProperties;
    }

    /**
     * Signs a message using the configured Ed25519 private key.
     *
     * @param message the raw message bytes to sign
     * @return Base64-encoded Ed25519 signature
     */
    public String sign(byte[] message) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(ondcProperties.getCrypto().getSigningPrivateKey());
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(privateKeyBytes, 0);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privateKey);
        signer.update(message, 0, message.length);
        byte[] signature = signer.generateSignature();
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * Signs a string message using Ed25519.
     */
    public String sign(String message) {
        return sign(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifies an Ed25519 signature against a message using the provided public key.
     *
     * @param message        the original message bytes
     * @param signatureB64   Base64-encoded signature
     * @param publicKeyB64   Base64-encoded Ed25519 public key
     * @return true if the signature is valid
     */
    public boolean verify(byte[] message, String signatureB64, String publicKeyB64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyB64);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureB64);
            Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(publicKeyBytes, 0);
            Ed25519Signer verifier = new Ed25519Signer();
            verifier.init(false, publicKey);
            verifier.update(message, 0, message.length);
            return verifier.verifySignature(signatureBytes);
        } catch (Exception e) {
            log.error("Ed25519 signature verification failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifies a string message signature.
     */
    public boolean verify(String message, String signatureB64, String publicKeyB64) {
        return verify(message.getBytes(StandardCharsets.UTF_8), signatureB64, publicKeyB64);
    }
}
