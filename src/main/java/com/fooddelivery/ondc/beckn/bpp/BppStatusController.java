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
 * BPP /status endpoint — BAP polling for current order/fulfillment status.
 * Returns ACK synchronously, triggers async /on_status callback with current state.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BppStatusController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;

    @PostMapping("/status")
    public ResponseEntity<OndcAckResponse> status(@RequestBody OndcRequest request) {
        log.info("Received /status from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());

        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("status")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        // TODO: Async processing — lookup current fulfillment state, send on_status callback

        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
