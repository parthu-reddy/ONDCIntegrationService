package com.fooddelivery.ondc.registry;

import com.fooddelivery.ondc.crypto.AesChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Handles the ONDC registry's /on_subscribe callback.
 * Receives the AES-encrypted challenge, decrypts it, and responds SYNCHRONOUSLY.
 * This is the critical handshake that activates the NP on the ONDC network.
 */
@RestController
public class OnSubscribeController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OnSubscribeController.class);
    private final AesChallengeService aesChallengeService;

    /**
     * POST /on_subscribe — Receives the AES challenge from ONDC registry.
     * MUST respond synchronously with the decrypted challenge string.
     */
    @PostMapping("/on_subscribe")
    public ResponseEntity<Map<String, String>> onSubscribe(@RequestBody Map<String, Object> payload) {
        log.info("Received /on_subscribe callback from ONDC registry");
        String challenge = (String) payload.get("challenge");
        if (challenge == null || challenge.isBlank()) {
            log.error("/on_subscribe payload missing \'challenge\' field: {}", payload);
            return ResponseEntity.badRequest().body(Map.of("answer", "", "error", "Missing challenge"));
        }
        try {
            String decrypted = aesChallengeService.decryptSubscriptionChallenge(challenge);
            log.info("/on_subscribe challenge decrypted successfully. Returning answer.");
            return ResponseEntity.ok(Map.of("answer", decrypted));
        } catch (Exception e) {
            log.error("Failed to decrypt /on_subscribe challenge: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("answer", "", "error", "Decryption failed"));
        }
    }

    @java.lang.SuppressWarnings("all")
    public OnSubscribeController(final AesChallengeService aesChallengeService) {
        this.aesChallengeService = aesChallengeService;
    }
}
