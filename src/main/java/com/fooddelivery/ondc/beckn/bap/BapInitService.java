package com.fooddelivery.ondc.beckn.bap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * BAP Init Service — sends /init to BPP with billing and delivery details.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class BapInitService {
    @java.lang.SuppressWarnings("all")

    private final RestTemplate ondcRestTemplate;
    private final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder;

    public void init(String bppUri, String transactionId, Object initDetails) {
        log.info("BAP sending /init to BPP: {}, transaction_id: {}", bppUri, transactionId);
        com.fooddelivery.ondc.dto.OndcContext context = contextBuilder.buildBapRequestContext("init", null, bppUri);
        context.setTransactionId(transactionId);
        com.fooddelivery.ondc.dto.OndcRequest request = new com.fooddelivery.ondc.dto.OndcRequest();
        request.setContext(context);
        com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();
        ondcMsg.setOrder(initDetails);
        request.setMessage(ondcMsg);
        ondcRestTemplate.postForEntity(bppUri + "/init", request, String.class);
    }

    @java.lang.SuppressWarnings("all")
    public BapInitService(final RestTemplate ondcRestTemplate, final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
    }
}
