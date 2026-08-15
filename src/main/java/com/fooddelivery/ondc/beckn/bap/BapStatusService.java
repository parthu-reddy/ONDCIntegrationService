package com.fooddelivery.ondc.beckn.bap;

import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcMessage;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@lombok.extern.slf4j.Slf4j
public class BapStatusService {
    @java.lang.SuppressWarnings("all")

    private final RestTemplate ondcRestTemplate;
    private final OndcContextBuilder contextBuilder;

    public void status(String bppId, String bppUri, String transactionId, String orderId) {
        log.info("BAP sending /status to BPP: {}, transaction_id: {}", bppUri, transactionId);
        try {
            OndcContext context = contextBuilder.buildBapRequestContext("status", bppId, bppUri);
            context.setTransactionId(transactionId);
            OndcRequest request = new OndcRequest();
            request.setContext(context);
            OndcMessage message = new OndcMessage();
            message.setOrderId(orderId);
            request.setMessage(message);
            String targetUrl = bppUri + "/status";
            ondcRestTemplate.postForEntity(targetUrl, request, String.class);
            log.info("Successfully sent /status to {}", targetUrl);
        } catch (Exception e) {
            log.error("Failed to send /status to BPP", e);
            throw new IllegalStateException("Failed to request order status from BPP", e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public BapStatusService(final RestTemplate ondcRestTemplate, final OndcContextBuilder contextBuilder) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
    }
}
