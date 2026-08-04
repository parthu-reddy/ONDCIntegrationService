package com.fooddelivery.ondc.beckn.callback;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** BAP /on_status callback — receives fulfillment state updates. */
@RestController @Slf4j @RequiredArgsConstructor
public class BapOnStatusController {
    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/on_status")
    public ResponseEntity<OndcAckResponse> onStatus(@RequestBody OndcRequest request) {
        log.info("Received /on_status, transaction_id: {}", request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
