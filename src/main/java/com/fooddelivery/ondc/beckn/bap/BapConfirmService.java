package com.fooddelivery.ondc.beckn.bap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * BAP Confirm Service — sends /confirm to BPP with payment proof.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class BapConfirmService {
    @java.lang.SuppressWarnings("all")

    private final RestTemplate ondcRestTemplate;
    private final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder;

    public void confirm(String bppUri, String transactionId, Object paymentDetails) {
        log.info("BAP sending /confirm to BPP: {}, transaction_id: {}", bppUri, transactionId);
        com.fooddelivery.ondc.dto.OndcContext context = contextBuilder.buildBapRequestContext("confirm", null, bppUri);
        context.setTransactionId(transactionId);
        com.fooddelivery.ondc.dto.OndcRequest request = new com.fooddelivery.ondc.dto.OndcRequest();
        request.setContext(context);
        com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();
        ondcMsg.setOrder(java.util.Map.of("payment", paymentDetails));
        request.setMessage(ondcMsg);
        ondcRestTemplate.postForEntity(bppUri + "/confirm", request, String.class);
    }

    @java.lang.SuppressWarnings("all")
    public BapConfirmService(final RestTemplate ondcRestTemplate, final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
    }
}
