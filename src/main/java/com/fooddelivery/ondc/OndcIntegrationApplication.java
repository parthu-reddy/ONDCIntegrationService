package com.fooddelivery.ondc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fooddelivery.ondc.config.OndcProperties;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
    scanBasePackages = {"com.fooddelivery.ondc", "com.fooddelivery.common"}
)
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = {"com.fooddelivery.ondc", "com.fooddelivery.common.repository"})
@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = {"com.fooddelivery.ondc", "com.fooddelivery.common.entity"})
@EnableDiscoveryClient

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(OndcProperties.class)
@com.fooddelivery.common.outbox.config.EnableOutbox
public class OndcIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OndcIntegrationApplication.class, args);
    }
}
