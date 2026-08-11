package com.fooddelivery.ondc.processor;

import com.fooddelivery.ondc.beckn.bap.BapSearchService;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Listens for internal search requests (from CustomerService) and
 * triggers ONDC BAP search.
 */
@Service
public class SearchEventProcessor {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SearchEventProcessor.class);
    private final BapSearchService bapSearchService;

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = OndcKafkaConfig.TOPIC_ONDC_SEARCH_REQUEST, groupId = "ondc-integration-group")
    public void handleSearchRequest(String eventJson) {
        log.info("Received internal search request: {}", eventJson);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> request = mapper.readValue(eventJson, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
            String city = (String) request.get("city");
            String gps = (String) request.get("gps");
            String searchKey = (String) request.get("searchKey");
            if (city != null && gps != null) {
                bapSearchService.search(city, gps, searchKey);
            } else {
                log.warn("Invalid search request payload: missing city or gps");
            }
        } catch (Exception e) {
            log.error("Failed to process search request event", e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public SearchEventProcessor(final BapSearchService bapSearchService) {
        this.bapSearchService = bapSearchService;
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}
