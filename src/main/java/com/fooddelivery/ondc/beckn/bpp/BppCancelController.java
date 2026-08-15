package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;
import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcError;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.fulfillment.FulfillmentStateMachine;
import com.fooddelivery.ondc.fulfillment.OndcFulfillmentState;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BPP /cancel endpoint — handles BAP-initiated order cancellation.
 * 
 * Idempotent: already-cancelled orders return ACK without error.
 * Already-delivered orders return NACK — cancellation is rejected.
 * 
 * CRITICAL: Must be idempotent per global rules — duplicate cancel
 * must not trigger double refund.
 */
@RestController
@lombok.extern.slf4j.Slf4j
public class BppCancelController {
    @java.lang.SuppressWarnings("all")

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;
    private final FulfillmentStateMachine fulfillmentStateMachine;

    @PostMapping("/cancel")
    @Transactional
    public ResponseEntity<OndcAckResponse> cancel(@RequestBody OndcRequest request) {
        log.info("Received /cancel from BAP: {}, transaction_id: {}", request.getContext().getBapId(), request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());
        String transactionId = request.getContext().getTransactionId();
        String messageId = request.getContext().getMessageId();
        // C4 fix: Idempotency check — prevent duplicate cancel processing
        boolean alreadyCancelled = transactionRepository.existsByTransactionIdAndMessageId(transactionId, messageId);
        if (alreadyCancelled) {
            log.warn("Duplicate /cancel detected for transaction_id: {}. Returning existing ACK.", transactionId);
            return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
        }
        // C5 fix: Validate order state — delivered orders cannot be cancelled
        OndcFulfillmentState currentState = fulfillmentStateMachine.getCurrentState(transactionId);
        if (currentState == OndcFulfillmentState.ORDER_DELIVERED) {
            log.warn("Cannot cancel delivered order. transaction_id: {}, state: {}", transactionId, currentState.getOndcValue());
            OndcError error = OndcError.builder().type("DOMAIN-ERROR").code("30019").message("Order already delivered — cancellation not allowed").build();
            return ResponseEntity.ok(OndcAckResponse.nack(request.getContext(), error));
        }
        // Log transaction
        OndcTransaction txn = OndcTransaction.builder().transactionId(transactionId).messageId(messageId).action("cancel").bapId(request.getContext().getBapId()).bppId(request.getContext().getBppId()).state("RECEIVED").build();
        transactionRepository.save(txn);
        // Dispatch async cancel processing — refund, state update, on_cancel callback
        callbackService.processCancelAsync(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BppCancelController(final OndcSchemaValidator schemaValidator, final OndcTransactionRepository transactionRepository, final BppCallbackService callbackService, final FulfillmentStateMachine fulfillmentStateMachine) {
        this.schemaValidator = schemaValidator;
        this.transactionRepository = transactionRepository;
        this.callbackService = callbackService;
        this.fulfillmentStateMachine = fulfillmentStateMachine;
    }
}
