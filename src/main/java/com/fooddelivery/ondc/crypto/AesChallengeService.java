package com.fooddelivery.ondc.crypto;

import com.fooddelivery.ondc.config.OndcProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles the AES decryption challenge sent by the ONDC registry
 * during the /on_subscribe callback.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AesChallengeService {

    private final X25519KeyManager x25519KeyManager;
    private final OndcProperties ondcProperties;

    /**
     * Decrypts the ONDC subscription challenge.
     * Uses the ONDC environment-specific public key to compute the shared secret.
     *
     * @param encryptedChallenge Base64-encoded AES-encrypted challenge string
     * @return the decrypted challenge string to return in the /on_subscribe response
     */
    public String decryptSubscriptionChallenge(String encryptedChallenge) {
        String ondcPublicKey = ondcProperties.getCrypto().getOndcProductionPublicKey();

        log.info("Decrypting ONDC subscription challenge...");
        String decrypted = x25519KeyManager.decryptChallenge(encryptedChallenge, ondcPublicKey);
        log.info("ONDC subscription challenge decrypted successfully");

        return decrypted;
    }
}
