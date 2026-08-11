package com.fooddelivery.ondc.beckn.inc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcMessage;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_CATALOG_DELTA;

/**
 * Listens for catalog delta events from RestaurantApplication (stock/price changes)
 * and pushes on_search_inc updates to subscribed BAPs.
 */
@Service
public class CatalogDeltaSyncService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CatalogDeltaSyncService.class);
    private final OndcContextBuilder contextBuilder;
    private final OndcProperties ondcProperties;
    private final RestTemplate ondcRestTemplate;
    private final ObjectMapper objectMapper;

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = TOPIC_ONDC_CATALOG_DELTA, groupId = "ondc-catalog-delta-group")
    public void handleCatalogDelta(String deltaEvent) {
        log.info("Received catalog delta event: {}", deltaEvent);
        try {
            // Parse delta event
            Map<String, Object> deltaPayload = objectMapper.readValue(deltaEvent, Map.class);
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
        } catch (Exception e) {
            log.error("Failed to process catalog delta event", e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public CatalogDeltaSyncService(final OndcContextBuilder contextBuilder, final OndcProperties ondcProperties, final RestTemplate ondcRestTemplate, final ObjectMapper objectMapper) {
        this.contextBuilder = contextBuilder;
        this.ondcProperties = ondcProperties;
        this.ondcRestTemplate = ondcRestTemplate;
        this.objectMapper = objectMapper;
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}
