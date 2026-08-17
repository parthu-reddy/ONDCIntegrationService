package com.fooddelivery.ondc.beckn.inc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcMessage;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import com.fooddelivery.common.entity.IdempotencyKey;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Map;
import java.util.UUID;
import static com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_CATALOG_DELTA;

/**
 * Listens for catalog delta events from RestaurantApplication (stock/price changes)
 * and pushes on_search_inc updates to subscribed BAPs.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class CatalogDeltaSyncService {

    private final OndcContextBuilder contextBuilder;
    private final OndcProperties ondcProperties;
    private final RestTemplate ondcRestTemplate;
    private final ObjectMapper objectMapper;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;

    public CatalogDeltaSyncService(OndcContextBuilder contextBuilder, OndcProperties ondcProperties, RestTemplate ondcRestTemplate, ObjectMapper objectMapper, IIdempotencyKeyRepository idempotencyKeyRepository) {
        this.contextBuilder = contextBuilder;
        this.ondcProperties = ondcProperties;
        this.ondcRestTemplate = ondcRestTemplate;
        this.objectMapper = objectMapper;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = TOPIC_ONDC_CATALOG_DELTA, groupId = "ondc-catalog-delta-group")
    public void handleCatalogDelta(String deltaEvent, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.info("Received catalog delta event: {}", deltaEvent);
        try {
            // Parse delta event
            Map<String, Object> deltaPayload = objectMapper.readValue(deltaEvent, Map.class);
            
            String extractedEventId = com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
            final String resolvedEventId;
            if (extractedEventId == null) {
                resolvedEventId = UUID.nameUUIDFromBytes(deltaEvent.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
            } else {
                resolvedEventId = extractedEventId;
            }

            String idempotencyKeyStr = "processed_event:" + resolvedEventId;

            if (idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                log.info("Duplicate catalog delta event ignored: {}", idempotencyKeyStr);
                return;
            }

            // In a full implementation, we'd look up the active subscriptions
            // from the database, but for now we broadcast to a configured Gateway or known BAP
            OndcContext context = contextBuilder.buildBapRequestContext("on_search", ondcProperties.getSubscriberId(), ondcProperties.getSubscriberUrl());
            context.setAction("on_search");
            // Set intent to indicate incremental update (inc)
            OndcRequest request = new OndcRequest();
            request.setContext(context);
            OndcMessage message = new OndcMessage();
            message.setCatalog(Map.of("bpp/providers", deltaPayload.get("providers")));
            request.setMessage(message);
            // Push to Gateway or subscribed BAPs
            String targetUrl = ondcProperties.getRegistry().getGatewayUrl() + "/on_search";
            log.info("Pushing catalog delta to: {}", targetUrl);
            ondcRestTemplate.postForEntity(targetUrl, request, String.class);

            try {
                if (!idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                    idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKeyStr));
                }
            } catch (Exception e) {
                log.warn("Failed to save idempotency key {}, but external action was completed", idempotencyKeyStr, e);
            }
        } catch (Exception e) {
            log.error("Failed to process catalog delta event", e);
        }
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}
