package com.fooddelivery.ondc.crypto;

import com.fooddelivery.ondc.config.OndcProperties;
import org.springframework.stereotype.Service;
import java.time.Instant;

/**
 * Constructs ONDC-compliant cryptographic signatures for outgoing requests.
 * 
 * Signing string format (exactly):
 *   (created): <unix_timestamp>
 *   (expires): <unix_timestamp>
 *   digest: BLAKE-512=<hash>
 * 
 * The signing string uses actual newline characters (\n), not escaped literals.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class SignatureService {
    @java.lang.SuppressWarnings("all")

    private final Ed25519KeyManager ed25519KeyManager;
    private final BlakeDigestService blakeDigestService;
    private final OndcProperties ondcProperties;
    private static final long DEFAULT_EXPIRY_SECONDS = 300; // 5 minutes

    /**
     * Constructs the full Authorization header value for an outgoing ONDC request.
     *
     * @param rawBody the raw HTTP request body bytes
     * @return the complete Authorization header value
     */
    public String createAuthorizationHeader(byte[] rawBody) {
        long created = Instant.now().getEpochSecond();
        long expires = created + DEFAULT_EXPIRY_SECONDS;
        String digest = blakeDigestService.computeDigestHeader(rawBody);
        String signingString = buildSigningString(created, expires, digest);
        String signature = ed25519KeyManager.sign(signingString);
        String keyId = ondcProperties.getSubscriberId() + "|" + ondcProperties.getUniqueKeyId() + "|ed25519";
        return String.format("Signature keyId=\"%s\",algorithm=\"ed25519\",created=\"%d\",expires=\"%d\"," + "headers=\"(created) (expires) digest\",signature=\"%s\"", keyId, created, expires, signature);
    }

    /**
     * Builds the deterministic signing string per ONDC spec.
     * CRITICAL: Uses actual newline characters, not "\\n" string literals.
     */
    public String buildSigningString(long created, long expires, String digest) {
        return "(created): " + created + "\n" + "(expires): " + expires + "\n" + "digest: " + digest;
    }

    /**
     * Extracts the keyId from an Authorization header.
     * Format: keyId="subscriber_id|unique_key_id|algorithm"
     */
    public String extractKeyId(String authHeader) {
        int start = authHeader.indexOf("keyId=\"") + 7;
        int end = authHeader.indexOf("\"", start);
        return authHeader.substring(start, end);
    }

    /**
     * Extracts the signature value from an Authorization header.
     */
    public String extractSignature(String authHeader) {
        int start = authHeader.indexOf("signature=\"") + 11;
        int end = authHeader.indexOf("\"", start);
        return authHeader.substring(start, end);
    }

    /**
     * Extracts the 'created' timestamp from an Authorization header.
     */
    public long extractCreated(String authHeader) {
        int start = authHeader.indexOf("created=\"") + 9;
        int end = authHeader.indexOf("\"", start);
        return Long.parseLong(authHeader.substring(start, end));
    }

    /**
     * Extracts the 'expires' timestamp from an Authorization header.
     */
    public long extractExpires(String authHeader) {
        int start = authHeader.indexOf("expires=\"") + 9;
        int end = authHeader.indexOf("\"", start);
        return Long.parseLong(authHeader.substring(start, end));
    }

    @java.lang.SuppressWarnings("all")
    public SignatureService(final Ed25519KeyManager ed25519KeyManager, final BlakeDigestService blakeDigestService, final OndcProperties ondcProperties) {
        this.ed25519KeyManager = ed25519KeyManager;
        this.blakeDigestService = blakeDigestService;
        this.ondcProperties = ondcProperties;
    }
}
