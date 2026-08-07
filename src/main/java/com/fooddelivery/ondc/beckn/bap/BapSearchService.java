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
public class BapSearchService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BapSearchService.class);
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
        ondcMsg.setIntent(java.util.Map.of("item", java.util.Map.of("descriptor", java.util.Map.of("name", searchKey)), "fulfillment", java.util.Map.of("type", "Delivery", "end", java.util.Map.of("location", java.util.Map.of("gps", gps)))));
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
