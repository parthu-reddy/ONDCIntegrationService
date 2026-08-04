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

/** BAP /on_init callback — receives locked quote + payment details from BPP. */
@RestController @Slf4j @RequiredArgsConstructor
public class BapOnInitController {
    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/on_init")
    public ResponseEntity<OndcAckResponse> onInit(@RequestBody OndcRequest request) {
        log.info("Received /on_init, transaction_id: {}", request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
