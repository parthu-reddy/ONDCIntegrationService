package com.fooddelivery.ondc.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Computes BLAKE-512 (BLAKE2b-512) hashes of HTTP request bodies.
 * ONDC mandates BLAKE-512 — SHA digests are categorically rejected.
 */
@Service
@Slf4j
public class BlakeDigestService {

    private static final int BLAKE2B_512_DIGEST_SIZE = 64; // 512 bits = 64 bytes

    /**
     * Computes BLAKE2b-512 hash of the raw request body bytes.
     *
     * @param body raw HTTP body bytes (must be exact bytes, not re-serialized)
     * @return Base64-encoded BLAKE-512 hash
     */
    public String computeDigest(byte[] body) {
        if (body == null) {
            // ONDC spec: empty body → hash of empty byte array, not null
            body = new byte[0];
        }

        Blake2bDigest digest = new Blake2bDigest(BLAKE2B_512_DIGEST_SIZE * 8);
        digest.update(body, 0, body.length);
        byte[] hash = new byte[BLAKE2B_512_DIGEST_SIZE];
        digest.doFinal(hash, 0);

        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Computes BLAKE2b-512 hash of a string body (normalized to UTF-8).
     */
    public String computeDigest(String body) {
        return computeDigest(body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0]);
    }

    /**
     * Constructs the digest header value in ONDC format.
     *
     * @param body raw request body bytes
     * @return formatted digest string: "BLAKE-512=<hash>"
     */
    public String computeDigestHeader(byte[] body) {
        return "BLAKE-512=" + computeDigest(body);
    }
}
