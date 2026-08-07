package com.fooddelivery.ondc.beckn.callback;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BAP /on_search callback — receives catalogs from BPPs. Merges results for the customer.
 */
@RestController
public class BapOnSearchController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BapOnSearchController.class);
    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/on_search")
    public ResponseEntity<OndcAckResponse> onSearch(@RequestBody OndcRequest request) {
        log.info("Received /on_search from BPP: {}, transaction_id: {}", request.getContext().getBppId(), request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        // TODO: Parse catalog, store/merge results, notify BAP frontend
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BapOnSearchController(final OndcSchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }
}
