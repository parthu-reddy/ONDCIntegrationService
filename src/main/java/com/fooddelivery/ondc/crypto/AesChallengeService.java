package com.fooddelivery.ondc.crypto;

import com.fooddelivery.ondc.config.OndcProperties;
import org.springframework.stereotype.Service;

/**
 * Handles the AES decryption challenge sent by the ONDC registry
 * during the /on_subscribe callback.
 */
@Service
public class AesChallengeService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AesChallengeService.class);
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

    @java.lang.SuppressWarnings("all")
    public AesChallengeService(final X25519KeyManager x25519KeyManager, final OndcProperties ondcProperties) {
        this.x25519KeyManager = x25519KeyManager;
        this.ondcProperties = ondcProperties;
    }
}
