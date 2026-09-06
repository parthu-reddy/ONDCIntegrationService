package com.fooddelivery.ondc.beckn.bap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@lombok.extern.slf4j.Slf4j
public class BapCancelService {
    @java.lang.SuppressWarnings("all")

    private final RestTemplate ondcRestTemplate;
    private final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder;

    public void cancel(String bppUri, String transactionId, String orderId, String cancellationReasonId) {
        log.info("BAP sending /cancel to BPP: {}, transaction_id: {}", bppUri, transactionId);
        com.fooddelivery.ondc.dto.OndcContext context = contextBuilder.buildBapRequestContext("cancel", null, bppUri);
        context.setTransactionId(transactionId);
        com.fooddelivery.ondc.dto.OndcRequest request = new com.fooddelivery.ondc.dto.OndcRequest();
        request.setContext(context);
        com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();
        ondcMsg.setOrder(com.fooddelivery.ondc.dto.OndcOrder.builder()
                .id(orderId)
                .cancellation(com.fooddelivery.ondc.dto.OndcOrder.OndcCancellation.builder()
                        .reason(com.fooddelivery.ondc.dto.OndcOrder.OndcCancellationReason.builder().id(cancellationReasonId).build())
                        .build())
                .build());
        request.setMessage(ondcMsg);
        ondcRestTemplate.postForEntity(bppUri + "/cancel", request, String.class);
    }

    @java.lang.SuppressWarnings("all")
    public BapCancelService(final RestTemplate ondcRestTemplate, final com.fooddelivery.ondc.util.OndcContextBuilder contextBuilder) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
    }
}
