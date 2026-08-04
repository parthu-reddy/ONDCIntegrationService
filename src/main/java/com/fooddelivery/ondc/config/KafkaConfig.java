package com.fooddelivery.ondc.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic definitions for ONDC event streaming.
 */
@Configuration
public class KafkaConfig {

    public static final String TOPIC_ONDC_SEARCH_REQUEST = "ondc.search.request";
    public static final String TOPIC_ONDC_ORDER_CREATED = "ondc.order.created";
    public static final String TOPIC_ONDC_ORDER_STATUS_CHANGED = "ondc.order.status.changed";
    public static final String TOPIC_ONDC_CATALOG_DELTA = "ondc.catalog.delta";
    public static final String TOPIC_ONDC_SETTLEMENT_EVENT = "ondc.settlement.event";
    public static final String TOPIC_ONDC_CALLBACK_DLQ = "ondc.callback.dlq";

    @Bean
    public NewTopic ondcSearchRequestTopic() {
        return TopicBuilder.name(TOPIC_ONDC_SEARCH_REQUEST)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ondcOrderCreatedTopic() {
        return TopicBuilder.name(TOPIC_ONDC_ORDER_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ondcOrderStatusChangedTopic() {
        return TopicBuilder.name(TOPIC_ONDC_ORDER_STATUS_CHANGED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ondcCatalogDeltaTopic() {
        return TopicBuilder.name(TOPIC_ONDC_CATALOG_DELTA)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ondcSettlementEventTopic() {
        return TopicBuilder.name(TOPIC_ONDC_SETTLEMENT_EVENT)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ondcCallbackDlqTopic() {
        return TopicBuilder.name(TOPIC_ONDC_CALLBACK_DLQ)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
