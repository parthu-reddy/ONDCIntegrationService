package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;
import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BPP /update endpoint — handles order modifications (partial cancel, returns, etc.).
 * Returns ACK synchronously, triggers async /on_update callback.
 */
@RestController
public class BppUpdateController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BppUpdateController.class);
    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;

    @PostMapping("/update")
    @Transactional
    public ResponseEntity<OndcAckResponse> update(@RequestBody OndcRequest request) {
        log.info("Received /update from BAP: {}, transaction_id: {}", request.getContext().getBapId(), request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());
        OndcTransaction txn = OndcTransaction.builder().transactionId(request.getContext().getTransactionId()).messageId(request.getContext().getMessageId()).action("update").bapId(request.getContext().getBapId()).bppId(request.getContext().getBppId()).state("RECEIVED").build();
        transactionRepository.save(txn);
        // Dispatch async on_update callback
        callbackService.processUpdateAsync(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BppUpdateController(final OndcSchemaValidator schemaValidator, final OndcTransactionRepository transactionRepository, final BppCallbackService callbackService) {
        this.schemaValidator = schemaValidator;
        this.transactionRepository = transactionRepository;
        this.callbackService = callbackService;
    }
}
