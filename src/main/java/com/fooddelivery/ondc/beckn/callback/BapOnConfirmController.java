package com.fooddelivery.ondc.beckn.callback;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BAP /on_confirm callback — receives order confirmation with fulfillment state from BPP.
 */
@RestController
@lombok.extern.slf4j.Slf4j
public class BapOnConfirmController {
    @java.lang.SuppressWarnings("all")

    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/on_confirm")
    public ResponseEntity<OndcAckResponse> onConfirm(@RequestBody OndcRequest request) {
        log.info("Received /on_confirm, transaction_id: {}", request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BapOnConfirmController(final OndcSchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }
}
