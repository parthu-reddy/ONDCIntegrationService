package com.fooddelivery.ondc.processor;

import com.fooddelivery.ondc.beckn.bap.BapSearchService;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
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
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Map;
import java.util.UUID;

/**
 * Listens for internal search requests (from CustomerService) and
 * triggers ONDC BAP search.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class SearchEventProcessor {

    private final BapSearchService bapSearchService;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;

    public SearchEventProcessor(BapSearchService bapSearchService, IIdempotencyKeyRepository idempotencyKeyRepository) {
        this.bapSearchService = bapSearchService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = OndcKafkaConfig.TOPIC_ONDC_SEARCH_REQUEST, groupId = "ondc-integration-group")
    public void handleSearchRequest(String eventJson, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.info("Received internal search request: {}", eventJson);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                    .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> request = mapper.readValue(eventJson, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });

            // BppSearchController publishes a serialized OndcRequest -- the Beckn envelope
            // {context, message}. `city` lives on the context (OndcContext.city carries no
            // @JsonProperty, so it serializes as context.city), NOT at the root, which is why the
            // previous root read was always null and every search was rejected below.
            com.fooddelivery.ondc.dto.OndcRequest ondcRequest =
                    mapper.readValue(eventJson, com.fooddelivery.ondc.dto.OndcRequest.class);
            com.fooddelivery.ondc.dto.OndcContext context = ondcRequest.getContext();
            String city = context != null ? context.getCity() : null;

            // gps and searchKey live inside message.intent, which OndcMessage models as a bare
            // Object. The paths are taken from this codebase's own Beckn serialization in
            // BapSearchService.search(), which builds exactly this structure when WE act as the BAP:
            //   intent.item.descriptor.name              -> searchKey
            //   intent.fulfillment.end.location.gps      -> gps
            // Reading them from the ROOT (as before) always yielded null, so every inbound search
            // was rejected by the guard below and no catalogue was ever returned.
            com.fasterxml.jackson.databind.JsonNode intent =
                    mapper.readTree(eventJson).path("message").path("intent");
            String gps = intent.path("fulfillment").path("end").path("location").path("gps").asText(null);
            String searchKey = intent.path("item").path("descriptor").path("name").asText(null);
            
            String extractedEventId = com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
            final String resolvedEventId;
            if (extractedEventId == null) {
                resolvedEventId = UUID.nameUUIDFromBytes(eventJson.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
            } else {
                resolvedEventId = extractedEventId;
            }
            
            String idempotencyKeyStr = "processed_event:" + resolvedEventId;

            if (idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                log.info("Duplicate search event ignored: {}", idempotencyKeyStr);
                return;
            }

            if (city != null && gps != null) {
                bapSearchService.search(city, gps, searchKey);
            } else {
                log.warn("Search request not dispatched -- the Beckn envelope is missing city "
                        + "(context.city) or gps (message.intent.fulfillment.end.location.gps). "
                        + "city={}, gps={}, transactionId={}",
                        city, gps, context != null ? context.getTransactionId() : null);
            }

            try {
                if (!idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                    idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKeyStr));
                }
            } catch (Exception e) {
                log.warn("Failed to save idempotency key {}, but external action was completed", idempotencyKeyStr, e);
            }
        } catch (Exception e) {
            log.error("Failed to process search request event", e);
        }
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}
