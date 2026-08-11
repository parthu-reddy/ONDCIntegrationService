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
    public RestTemplate ondcRestTemplate(org.springframework.boot.web.client.RestTemplateBuilder builder, com.fooddelivery.ondc.crypto.SignatureService signatureService) {
        RestTemplate restTemplate = builder.build();
        restTemplate.getInterceptors().add(new com.fooddelivery.ondc.auth.OndcRequestInterceptor(signatureService));
        return restTemplate;
    }
}
