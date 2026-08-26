package com.fooddelivery.ondc.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = BaseMessagingClass.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"})
@org.springframework.test.context.ActiveProfiles("contract-test")
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = {"ondc.order.created", "ondc.settlement.event", "ondc.search.request"})
public abstract class BaseMessagingClass {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public KafkaMessageVerifier kafkaMessageVerifier() {
            return new KafkaMessageVerifier();
        }
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> System.getProperty("spring.embedded.kafka.brokers", "localhost:9092"));
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /** Mirrors BppConfirmController: a real OndcRequest envelope, keyed by transaction id. */
    public void fireOndcOrderCreated() throws Exception {
        String transactionId = "2a96c231-e034-5303-8d4a-dab305dbba8b";
        com.fooddelivery.ondc.dto.OndcContext context = com.fooddelivery.ondc.dto.OndcContext.builder()
                .domain("ONDC:RET11")
                .action("confirm")
                .coreVersion("1.2.0")
                .bapId("buyer-app.example.com")
                .bapUri("https://buyer-app.example.com")
                .bppId("seller-app.example.com")
                .bppUri("https://seller-app.example.com")
                .transactionId(transactionId)
                .messageId("a60c37a3-d052-5ecf-96e4-423a952f27c1")
                .build();
        com.fooddelivery.ondc.dto.OndcRequest request = com.fooddelivery.ondc.dto.OndcRequest.builder()
                .context(context)
                .build();
        kafkaTemplate.send(com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_ORDER_CREATED, transactionId,
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request));
    }

    /**
     * Drives the REAL BppSearchController.search(), the only publisher to ondc.search.request.
     * Only the schema validator and the audit repository are mocked; the controller holds the real
     * auto-configured KafkaTemplate, so the topic, the Kafka key and the serialization are all the
     * production ones.
     *
     * What this pins, and why each part matters:
     *
     *   1. The topic is the dotted ondc.search.request from OndcKafkaConfig. ONDC topics use DOTS.
     *      Two earlier ONDC contracts asserted hyphenated topics nothing published to and passed
     *      anyway, because their triggers hardcoded the same wrong literal.
     *   2. The body is a serialized OndcRequest -- the Beckn {context, message} envelope. Context
     *      keys are snake_case (@JsonProperty on OndcContext), so transaction_id, bap_id and
     *      core_version, NOT camelCase. SearchEventProcessor read these at the root in camelCase
     *      and every inbound search was silently discarded.
     *   3. message.intent carries the search terms at the exact depths the consumer reads:
     *      intent.item.descriptor.name and intent.fulfillment.end.location.gps. The structure is
     *      taken from BapSearchService.search(), which builds it when we act as the BAP.
     *
     * OndcMessage models intent as a bare Object, so nothing but this contract stops those paths
     * from drifting.
     */
    public void fireOndcSearchRequest() throws Exception {
        String transactionId = "6f9619ff-8b86-5d11-b42d-00cf4fc964ff";
        com.fooddelivery.ondc.dto.OndcContext context = com.fooddelivery.ondc.dto.OndcContext.builder()
                .domain("ONDC:RET11")
                .action("search")
                .country("IND")
                .city("std:080")
                .coreVersion("1.2.0")
                .bapId("buyer-app.example.com")
                .bapUri("https://buyer-app.example.com")
                .transactionId(transactionId)
                .messageId("a60c37a3-d052-5ecf-96e4-423a952f27c1")
                .timestamp("2026-08-19T10:15:30.000Z")
                .build();

        // Exactly the structure BapSearchService.search() builds.
        com.fooddelivery.ondc.dto.OndcMessage message = new com.fooddelivery.ondc.dto.OndcMessage();
        message.setIntent(java.util.Map.of(
                "item", java.util.Map.of("descriptor", java.util.Map.of("name", "biryani")),
                "fulfillment", java.util.Map.of(
                        "type", "Delivery",
                        "end", java.util.Map.of("location", java.util.Map.of("gps", "12.971598,77.594562")))));

        com.fooddelivery.ondc.dto.OndcRequest request = com.fooddelivery.ondc.dto.OndcRequest.builder()
                .context(context)
                .message(message)
                .build();

        new com.fooddelivery.ondc.beckn.bpp.BppSearchController(
                org.mockito.Mockito.mock(com.fooddelivery.ondc.util.OndcSchemaValidator.class),
                org.mockito.Mockito.mock(com.fooddelivery.ondc.repository.OndcTransactionRepository.class),
                kafkaTemplate).search(request);
    }

    /** Mirrors SettlementService: amount is interpolated unquoted, so it is a JSON number. */
    public void fireOndcSettlementEvent() {
        String transactionId = "0f1a5cb3-2b6d-5a1e-9c47-8e3f6d2a1b04";
        String eventJson = String.format("{\"transactionId\":\"%s\",\"type\":\"%s\",\"amount\":%s,\"currency\":\"%s\"}",
                transactionId, "COLLECT", new java.math.BigDecimal("250.00"), "INR");
        kafkaTemplate.send(com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_SETTLEMENT_EVENT, transactionId, eventJson);
    }

}
