package com.fooddelivery.contract;

import com.fooddelivery.ondc.client.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("contract-test")
@SpringBootTest(classes = ONDCContractConsumerTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    // Stub ids are Maven artifactIds; Feign resolves by spring.application.name. These two
    // differ for these services, so the stub must be registered under the name the client asks for.
    "stubrunner.idsToServiceIds.restaurant-application=restaurant-service",
    "stubrunner.idsToServiceIds.delivery-executive-application=delivery-service"
})
@AutoConfigureStubRunner(ids = { "com.fooddelivery:restaurant-application:+:stubs:8091", "com.fooddelivery:delivery-executive-application:+:stubs:8092" }, stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class ONDCContractConsumerTest {


    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @EnableFeignClients(basePackages = "com.fooddelivery.ondc.client")
    static class TestConfig {
    }

    @Autowired
    private RestaurantServiceClient restaurantServiceClient;

    @Test
    public void shouldFetchRestaurantOutlets() {
        Map<String, Object> response = restaurantServiceClient.getServiceableRestaurants(12.9716, 77.5946, 5.0);
        
        assertNotNull(response);
        assertEquals(Boolean.TRUE, response.get("success"));
        assertNotNull(response.get("data"));
    }
}
