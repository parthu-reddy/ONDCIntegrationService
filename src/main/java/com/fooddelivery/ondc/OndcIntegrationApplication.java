package com.fooddelivery.ondc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fooddelivery.ondc.config.OndcProperties;

@SpringBootApplication(scanBasePackages = {
        "com.fooddelivery.ondc",
        "com.fooddelivery.common"
})

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(OndcProperties.class)
public class OndcIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OndcIntegrationApplication.class, args);
    }
}
