package com.fooddelivery.ondc.beckn.inc;

import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.fooddelivery.ondc.config.KafkaConfig.TOPIC_ONDC_CATALOG_DELTA;

/**
 * Listens for catalog delta events from RestaurantApplication (stock/price changes)
 * and pushes on_search_inc updates to subscribed BAPs.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogDeltaSyncService {

    private final OndcContextBuilder contextBuilder;
    private final OndcProperties ondcProperties;
    private final RestTemplate ondcRestTemplate;

    @KafkaListener(topics = TOPIC_ONDC_CATALOG_DELTA, groupId = "ondc-catalog-delta-group")
    public void handleCatalogDelta(String deltaEvent) {
        log.info("Received catalog delta event: {}", deltaEvent);

        // TODO: Parse delta event, build on_search_inc payload,
        //       determine subscribed BAPs, and push delta callbacks
    }
}
