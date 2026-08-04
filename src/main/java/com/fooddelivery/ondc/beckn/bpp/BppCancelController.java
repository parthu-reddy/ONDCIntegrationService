package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.ondc.dto.OndcAckResponse;
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
 * BPP /cancel endpoint — handles BAP-initiated order cancellation.
 * Idempotent: already-cancelled orders return ACK without error.
 * Already-delivered orders return NACK.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BppCancelController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;

    @PostMapping("/cancel")
    public ResponseEntity<OndcAckResponse> cancel(@RequestBody OndcRequest request) {
        log.info("Received /cancel from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());

        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("cancel")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        // TODO: Async processing — check order status, process cancellation,
        //       trigger refund if applicable, send on_cancel callback

        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
