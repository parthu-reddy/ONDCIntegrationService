package com.fooddelivery.ondc.processor;

import com.fooddelivery.ondc.beckn.bap.BapSearchService;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Listens for internal search requests (from CustomerService) and
 * triggers ONDC BAP search.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchEventProcessor {

    private final BapSearchService bapSearchService;

    @KafkaListener(topics = OndcKafkaConfig.TOPIC_ONDC_SEARCH_REQUEST, groupId = "ondc-integration-group")
    public void handleSearchRequest(String eventJson) {
        log.info("Received internal search request: {}", eventJson);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> request = mapper.readValue(eventJson, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            
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
}
