package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcError;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BPP /select endpoint — receives cart items from BAP.
 * Validates inventory, calculates taxes/charges.
 * Returns ACK synchronously, triggers async /on_select callback with quote breakdown.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BppSelectController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;

    @PostMapping("/select")
    public ResponseEntity<OndcAckResponse> select(@RequestBody OndcRequest request) {
        log.info("Received /select from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());

        // Log transaction
        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("select")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        // TODO: Async processing — inventory check, tax calculation, on_select callback
        // This will be handled by a Kafka consumer or @Async service

        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
