package com.fooddelivery.ondc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate bean for outbound ONDC HTTP calls (registry, callbacks).
 * The OndcRequestInterceptor is added to sign outgoing requests.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate ondcRestTemplate(org.springframework.boot.web.client.RestTemplateBuilder builder, com.fooddelivery.ondc.auth.OndcRequestInterceptor interceptor) {
        RestTemplate restTemplate = builder.build();
        restTemplate.getInterceptors().add(interceptor);
        return restTemplate;
    }
}
