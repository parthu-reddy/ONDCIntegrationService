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

@SpringBootTest(classes = BaseMessagingClass.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@org.springframework.test.context.ActiveProfiles("contract-test")
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = {"ondc.order.created", "ondc.settlement.event"})
public abstract class BaseMessagingClass {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
            org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
            org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class,
            org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class
    })
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

    /** Mirrors SettlementService: amount is interpolated unquoted, so it is a JSON number. */
    public void fireOndcSettlementEvent() {
        String transactionId = "0f1a5cb3-2b6d-5a1e-9c47-8e3f6d2a1b04";
        String eventJson = String.format("{\"transactionId\":\"%s\",\"type\":\"%s\",\"amount\":%s,\"currency\":\"%s\"}",
                transactionId, "COLLECT", new java.math.BigDecimal("250.00"), "INR");
        kafkaTemplate.send(com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_SETTLEMENT_EVENT, transactionId, eventJson);
    }

}
