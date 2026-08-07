package com.fooddelivery.ondc.beckn.bap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * BAP Select Service — sends /select to a specific BPP with cart items.
 */
@Service
public class BapSelectService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BapSelectService.class);
    private final RestTemplate ondcRestTemplate;
    private final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder;

    public void select(String bppUri, String transactionId, Object selectDetails) {
        log.info("BAP sending /select to BPP: {}, transaction_id: {}", bppUri, transactionId);
        com.fooddelivery.ondc.dto.OndcContext context = contextBuilder.buildBapRequestContext("select", null, bppUri);
        context.setTransactionId(transactionId);
        com.fooddelivery.ondc.dto.OndcRequest request = new com.fooddelivery.ondc.dto.OndcRequest();
        request.setContext(context);
        com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();
        ondcMsg.setOrder(selectDetails);
        request.setMessage(ondcMsg);
        ondcRestTemplate.postForEntity(bppUri + "/select", request, String.class);
    }

    @java.lang.SuppressWarnings("all")
    public BapSelectService(final RestTemplate ondcRestTemplate, final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
    }
}
