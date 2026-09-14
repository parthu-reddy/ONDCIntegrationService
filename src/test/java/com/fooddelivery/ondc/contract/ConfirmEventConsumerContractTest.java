package com.fooddelivery.ondc.contract;

import com.fooddelivery.common.contract.KafkaStubMessageSender;

import com.fooddelivery.ondc.beckn.bap.BapConfirmService;
import com.fooddelivery.ondc.processor.ConfirmEventProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Consumes the real ondc_order_created stub and asserts the BAP confirm callback is triggered.
 *
 * Before the fix this processor read {@code transactionId} and {@code bppUri} at the ROOT in
 * camelCase, while the producer publishes a serialized OndcRequest whose context maps to snake_case
 * ({@code context.transaction_id}). Both were always null, so it logged "Invalid confirm request
 * payload" and BapConfirmService.confirm was never called -- every ONDC order failed to confirm.
 */
@SpringBootTest(classes = ConfirmEventConsumerContractTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration")
@ActiveProfiles("contract-test")
@AutoConfigureStubRunner(ids = "com.fooddelivery:ondc-integration-service:+:stubs")
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS)
@EmbeddedKafka(partitions = 1, topics = {"ondc.order.created"})
class ConfirmEventConsumerContractTest {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    
    // EventBinder: the consumer now needs one, and a sliced context does not inherit the
// application's scan of com.fooddelivery.common. Imported directly rather than via a
// helper @Configuration in common-test -- such a class sits in an unlayered package and
// depending on ..event.. (the Service layer) fails ArchitectureEnforcementTest in every
// module. Spring builds it from the context's ObjectMapper and Validator.
@Import({ConfirmEventProcessor.class, com.fooddelivery.common.event.EventBinder.class})
    static class TestConfig {
        @Bean
        public MessageVerifierSender<Message<?>> kafkaStubMessageSender(KafkaTemplate<String, String> t) {
            return new KafkaStubMessageSender(t);
        }
    }

    @MockBean
    private BapConfirmService bapConfirmService;

    @MockBean
    private com.fooddelivery.common.repository.IIdempotencyKeyRepository idempotencyKeyRepository;

    @Autowired
    private StubTrigger stubTrigger;

    @Test
    void invokesTheBapConfirmCallbackFromTheProducerStub() {
        stubTrigger.trigger("ondc_order_created");

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() ->
                verify(bapConfirmService).confirm(
                        eq("https://seller-app.example.com"), any(), any()));
    }
}
