package com.fooddelivery.ondc.registry;

import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.exception.OndcRegistryException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Queries the ONDC /v2.0/lookup registry API to retrieve network participants'
 * public keys. Caches results in Redis with configurable TTL.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegistryLookupService {

    private final OndcProperties ondcProperties;
    private final RestTemplate ondcRestTemplate;
    private final RedisTemplate<String, Object> ondcRedisTemplate;

    private static final String CACHE_KEY_PREFIX = "ondc:registry:";

    /**
     * Looks up a network participant's signing public key by subscriber_id and unique_key_id.
     * Checks Redis cache first, falls back to ONDC registry API.
     *
     * @param subscriberId the NP's subscriber_id
     * @param uniqueKeyId  the NP's unique_key_id
     * @return Base64-encoded Ed25519 signing public key
     * @throws OndcRegistryException if lookup fails from both cache and registry
     */
    @CircuitBreaker(name = "ondcRegistry", fallbackMethod = "lookupFromCacheFallback")
    public String lookupSigningPublicKey(String subscriberId, String uniqueKeyId) {
        // 1. Check Redis cache first
        String cacheKey = CACHE_KEY_PREFIX + subscriberId + ":" + uniqueKeyId;
        String cachedKey = (String) ondcRedisTemplate.opsForValue().get(cacheKey);
        if (cachedKey != null) {
            log.debug("Registry cache hit for subscriber: {}", subscriberId);
            return cachedKey;
        }

        // 2. Query ONDC registry
        log.info("Registry cache miss. Querying ONDC registry for subscriber: {}", subscriberId);
        String registryUrl = ondcProperties.getRegistry().getUrl() + "/v2.0/lookup";

        Map<String, String> requestBody = Map.of(
                "subscriber_id", subscriberId,
                "ukId", uniqueKeyId
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object>[] response = ondcRestTemplate.postForObject(
                    registryUrl, requestBody, Map[].class);

            if (response == null || response.length == 0) {
                throw new OndcRegistryException(
                        "No registry entry found for subscriber: " + subscriberId);
            }

            String signingPublicKey = (String) response[0].get("signing_public_key");
            if (signingPublicKey == null || signingPublicKey.isBlank()) {
                throw new OndcRegistryException(
                        "signing_public_key missing in registry response for: " + subscriberId);
            }

            // 3. Cache in Redis
            int ttl = ondcProperties.getRegistry().getLookupCacheTtlSeconds();
            ondcRedisTemplate.opsForValue().set(cacheKey, signingPublicKey, ttl, TimeUnit.SECONDS);
            log.info("Cached registry entry for subscriber: {} (TTL: {}s)", subscriberId, ttl);

            return signingPublicKey;
        } catch (OndcRegistryException e) {
            throw e;
        } catch (Exception e) {
            throw new OndcRegistryException(
                    "Failed to query ONDC registry for subscriber: " + subscriberId, e);
        }
    }

    /**
     * Circuit breaker fallback — tries Redis cache when registry is unavailable.
     */
    @SuppressWarnings("unused")
    private String lookupFromCacheFallback(String subscriberId, String uniqueKeyId, Throwable t) {
        log.warn("Registry circuit breaker open. Attempting cache fallback for: {}", subscriberId);
        String cacheKey = CACHE_KEY_PREFIX + subscriberId + ":" + uniqueKeyId;
        String cachedKey = (String) ondcRedisTemplate.opsForValue().get(cacheKey);

        if (cachedKey != null) {
            log.info("Cache fallback successful for subscriber: {}", subscriberId);
            return cachedKey;
        }

        throw new OndcRegistryException(
                "Registry unavailable and no cached key for subscriber: " + subscriberId, t);
    }
}
