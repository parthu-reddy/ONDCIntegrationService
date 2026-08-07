package com.fooddelivery.ondc.beckn.callback;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BAP /on_track callback — receives tracking URL from BPP.
 */
@RestController
public class BapOnTrackController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BapOnTrackController.class);
    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/on_track")
    public ResponseEntity<OndcAckResponse> onTrack(@RequestBody OndcRequest request) {
        log.info("Received /on_track, transaction_id: {}", request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BapOnTrackController(final OndcSchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }
}
