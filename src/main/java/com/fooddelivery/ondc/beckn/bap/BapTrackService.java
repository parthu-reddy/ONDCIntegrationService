package com.fooddelivery.ondc.beckn.bap;

import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcMessage;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BapTrackService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BapTrackService.class);
    private final RestTemplate ondcRestTemplate;
    private final OndcContextBuilder contextBuilder;

    public void track(String bppId, String bppUri, String transactionId, String orderId) {
        log.info("BAP sending /track to BPP: {}, transaction_id: {}", bppUri, transactionId);
        try {
            OndcContext context = contextBuilder.buildBapRequestContext("track", bppId, bppUri);
            context.setTransactionId(transactionId);
            OndcRequest request = new OndcRequest();
            request.setContext(context);
            OndcMessage message = new OndcMessage();
            message.setOrderId(orderId);
            request.setMessage(message);
            String targetUrl = bppUri + "/track";
            ondcRestTemplate.postForEntity(targetUrl, request, String.class);
            log.info("Successfully sent /track to {}", targetUrl);
        } catch (Exception e) {
            log.error("Failed to send /track to BPP", e);
            throw new IllegalStateException("Failed to request order tracking from BPP", e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public BapTrackService(final RestTemplate ondcRestTemplate, final OndcContextBuilder contextBuilder) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
    }
}
