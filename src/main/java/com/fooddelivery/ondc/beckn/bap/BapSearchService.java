package com.fooddelivery.ondc.beckn.bap;

import com.fooddelivery.ondc.auth.OndcRequestInterceptor;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * BAP Search Service — broadcasts /search intent to ONDC Gateway.
 * Results arrive asynchronously via BapOnSearchController.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class BapSearchService {
    @java.lang.SuppressWarnings("all")

    private final OndcContextBuilder contextBuilder;
    private final RestTemplate ondcRestTemplate;

    /**
     * Sends a /search request to the ONDC Gateway for discovery.
     *
     * @param city      ONDC city code (e.g., "std:080")
     * @param gps       GPS coordinates "lat,lng"
     * @param searchKey search keyword
     */
    public void search(String city, String gps, String searchKey) {
        log.info("BAP initiating /search — city: {}, gps: {}, key: {}", city, gps, searchKey);
        com.fooddelivery.ondc.dto.OndcContext context = contextBuilder.buildBapRequestContext("search", null, null);
        OndcRequest request = new OndcRequest();
        request.setContext(context);
        com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();
        ondcMsg.setIntent(com.fooddelivery.ondc.dto.OndcIntent.builder()
                .item(com.fooddelivery.ondc.dto.OndcIntent.OndcItem.builder()
                        .descriptor(com.fooddelivery.ondc.dto.OndcIntent.OndcDescriptor.builder().name(searchKey).build())
                        .build())
                .fulfillment(com.fooddelivery.ondc.dto.OndcIntent.OndcFulfillment.builder()
                        .type("Delivery")
                        .end(com.fooddelivery.ondc.dto.OndcIntent.OndcFulfillmentEnd.builder()
                                .location(com.fooddelivery.ondc.dto.OndcIntent.OndcLocation.builder().gps(gps).build())
                                .build())
                        .build())
                .build());
        request.setMessage(ondcMsg);
        String gatewayUrl = contextBuilder.getProperties().getRegistry().getGatewayUrl();
        String searchEndpoint = gatewayUrl + "/search";
        log.debug("Sending search request to gateway: {}", searchEndpoint);
        ondcRestTemplate.postForEntity(searchEndpoint, request, String.class);
    }

    @java.lang.SuppressWarnings("all")
    public BapSearchService(final OndcContextBuilder contextBuilder, final RestTemplate ondcRestTemplate) {
        this.contextBuilder = contextBuilder;
        this.ondcRestTemplate = ondcRestTemplate;
    }
}
