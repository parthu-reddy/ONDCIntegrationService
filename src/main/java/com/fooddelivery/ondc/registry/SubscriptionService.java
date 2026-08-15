package com.fooddelivery.ondc.registry;

import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.crypto.Ed25519KeyManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the ONDC subscription lifecycle.
 * Builds and submits /subscribe payloads to the ONDC registry.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class SubscriptionService {
    @java.lang.SuppressWarnings("all")

    private final OndcProperties ondcProperties;
    private final Ed25519KeyManager ed25519KeyManager;
    private final RestTemplate ondcRestTemplate;

    /**
     * Submits a /subscribe POST request to the ONDC registry.
     * This initiates the onboarding process.
     *
     * @param subscriberUrl the callback URL for /on_subscribe
     * @param opsNo         operational role: 1=BAP, 2=BPP, 4=BAP+BPP
     * @return the request_id used for tracking
     */
    public String subscribe(String subscriberUrl, int opsNo) {
        String requestId = UUID.randomUUID().toString();
        String signedRequestId = ed25519KeyManager.sign(requestId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("subscriber_id", ondcProperties.getSubscriberId());
        payload.put("subscriber_url", subscriberUrl);
        payload.put("signing_public_key", ondcProperties.getCrypto().getSigningPublicKey());
        payload.put("encryption_public_key", ondcProperties.getCrypto().getEncryptionPublicKey());
        payload.put("unique_key_id", ondcProperties.getUniqueKeyId());
        payload.put("request_id", requestId);
        payload.put("ops_no", opsNo);
        payload.put("domain", ondcProperties.getDomain());
        payload.put("city", ondcProperties.getCity());
        payload.put("country", ondcProperties.getCountry());
        String registryUrl = ondcProperties.getRegistry().getUrl() + "/subscribe";
        log.info("Submitting ONDC /subscribe to {} with subscriber_id: {}, ops_no: {}", registryUrl, ondcProperties.getSubscriberId(), opsNo);
        try {
            ondcRestTemplate.postForEntity(registryUrl, payload, Map.class);
            log.info("ONDC /subscribe submitted successfully. Awaiting /on_subscribe challenge. requestId: {}", requestId);
        } catch (Exception e) {
            log.error("Failed to submit ONDC /subscribe: {}", e.getMessage(), e);
            throw new IllegalStateException("ONDC subscription failed", e);
        }
        return requestId;
    }

    @java.lang.SuppressWarnings("all")
    public SubscriptionService(final OndcProperties ondcProperties, final Ed25519KeyManager ed25519KeyManager, final RestTemplate ondcRestTemplate) {
        this.ondcProperties = ondcProperties;
        this.ed25519KeyManager = ed25519KeyManager;
        this.ondcRestTemplate = ondcRestTemplate;
    }
}
