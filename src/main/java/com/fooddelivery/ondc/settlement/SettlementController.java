package com.fooddelivery.ondc.settlement;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Settlement and reconciliation callback endpoints (RSF 2.0).
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SettlementController {

    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/settle")
    public ResponseEntity<OndcAckResponse> settle(@RequestBody OndcRequest request) {
        log.info("Received /settle");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/on_settle")
    public ResponseEntity<OndcAckResponse> onSettle(@RequestBody OndcRequest request) {
        log.info("Received /on_settle");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/recon")
    public ResponseEntity<OndcAckResponse> recon(@RequestBody OndcRequest request) {
        log.info("Received /recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/on_recon")
    public ResponseEntity<OndcAckResponse> onRecon(@RequestBody OndcRequest request) {
        log.info("Received /on_recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/receiver_recon")
    public ResponseEntity<OndcAckResponse> receiverRecon(@RequestBody OndcRequest request) {
        log.info("Received /receiver_recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/on_receiver_recon")
    public ResponseEntity<OndcAckResponse> onReceiverRecon(@RequestBody OndcRequest request) {
        log.info("Received /on_receiver_recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
