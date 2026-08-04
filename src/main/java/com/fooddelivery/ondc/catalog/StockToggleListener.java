package com.fooddelivery.ondc.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.fooddelivery.ondc.config.KafkaConfig.TOPIC_ONDC_CATALOG_DELTA;

/**
 * Listens for stock/price change events from RestaurantApplication
 * and triggers on_search_inc incremental catalog updates.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StockToggleListener {

    @KafkaListener(topics = TOPIC_ONDC_CATALOG_DELTA, groupId = "ondc-stock-toggle-group")
    public void handleStockChange(String event) {
        log.info("Stock/price change detected: {}", event);
        // TODO: Parse event, determine affected items, push on_search_inc delta
    }
}
