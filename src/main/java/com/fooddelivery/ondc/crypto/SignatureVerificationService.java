package com.fooddelivery.ondc.crypto;

import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.exception.OndcSignatureException;
import com.fooddelivery.ondc.registry.RegistryLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Verifies incoming ONDC request signatures.
 * Extracts keyId from Authorization header, performs registry lookup for the
 * sender's public key, reconstructs the signing string, and validates the Ed25519 signature.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SignatureVerificationService {

    private final Ed25519KeyManager ed25519KeyManager;
    private final BlakeDigestService blakeDigestService;
    private final SignatureService signatureService;
    private final RegistryLookupService registryLookupService;
    private final OndcProperties ondcProperties;

    /**
     * Verifies the Authorization header signature on an incoming ONDC request.
     *
     * @param authHeader the Authorization or X-Gateway-Authorization header value
     * @param rawBody    the raw HTTP body bytes (cached before deserialization)
     * @throws OndcSignatureException if verification fails
     */
    public void verifySignature(String authHeader, byte[] rawBody) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new OndcSignatureException("Missing Authorization header");
        }

        // Extract components from the header
        String keyId = signatureService.extractKeyId(authHeader);
        String signature = signatureService.extractSignature(authHeader);
        long created = signatureService.extractCreated(authHeader);
        long expires = signatureService.extractExpires(authHeader);

        // 1. Validate timestamps (replay attack prevention)
        long now = Instant.now().getEpochSecond();
        int tolerance = ondcProperties.getClockSkewToleranceSeconds();

        if (expires < now - tolerance) {
            throw new OndcSignatureException(
                    "Signature expired. Expires: " + expires + ", Current: " + now);
        }

        if (created > now + tolerance) {
            throw new OndcSignatureException(
                    "Signature created in the future. Created: " + created + ", Current: " + now);
        }

        // 2. Parse keyId: "subscriber_id|unique_key_id|algorithm"
        String[] keyParts = keyId.split("\\|");
        if (keyParts.length < 2) {
            throw new OndcSignatureException("Invalid keyId format: " + keyId);
        }
        String subscriberId = keyParts[0];
        String uniqueKeyId = keyParts[1];

        // 3. Lookup sender's public key from registry (with Redis cache)
        String senderPublicKey = registryLookupService.lookupSigningPublicKey(subscriberId, uniqueKeyId);

        // 4. Recompute BLAKE-512 digest
        String digest = blakeDigestService.computeDigestHeader(rawBody);

        // 5. Reconstruct the signing string
        String signingString = signatureService.buildSigningString(created, expires, digest);

        // 6. Verify Ed25519 signature
        boolean valid = ed25519KeyManager.verify(signingString, signature, senderPublicKey);

        if (!valid) {
            log.error("ONDC signature verification FAILED for subscriber: {}, keyId: {}",
                    subscriberId, uniqueKeyId);
            throw new OndcSignatureException(
                    "Ed25519 signature verification failed for subscriber: " + subscriberId);
        }

        log.debug("ONDC signature verified successfully for subscriber: {}", subscriberId);
    }
}
