package com.fooddelivery.ondc.util;

import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.dto.OndcContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility to build ONDC context objects for outgoing messages.
 */
@Component
@RequiredArgsConstructor
public class OndcContextBuilder {

    private final OndcProperties ondcProperties;

    public OndcProperties getProperties() {
        return ondcProperties;
    }

    /**
     * Creates a BPP response context from the incoming BAP request context.
     * Preserves transaction_id and message_id, fills in BPP details.
     */
    public OndcContext buildBppResponseContext(OndcContext incomingContext, String action) {
        return OndcContext.builder()
                .domain(ondcProperties.getDomain())
                .action(action)
                .country(ondcProperties.getCountry())
                .city(incomingContext.getCity() != null ? incomingContext.getCity() : ondcProperties.getCity())
                .coreVersion("1.2.0")
                .bapId(incomingContext.getBapId())
                .bapUri(incomingContext.getBapUri())
                .bppId(ondcProperties.getSubscriberId())
                .bppUri(incomingContext.getBppUri())
                .transactionId(incomingContext.getTransactionId())
                .messageId(UUID.randomUUID().toString())
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .build();
    }

    /**
     * Creates a BAP outgoing context for initiating requests.
     */
    public OndcContext buildBapRequestContext(String action, String bppId, String bppUri) {
        return OndcContext.builder()
                .domain(ondcProperties.getDomain())
                .action(action)
                .country(ondcProperties.getCountry())
                .city(ondcProperties.getCity())
                .coreVersion("1.2.0")
                .bapId(ondcProperties.getSubscriberId())
                .bapUri(ondcProperties.getSubscriberId()) // subscriber_url
                .bppId(bppId)
                .bppUri(bppUri)
                .transactionId(UUID.randomUUID().toString())
                .messageId(UUID.randomUUID().toString())
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .build();
    }
}
