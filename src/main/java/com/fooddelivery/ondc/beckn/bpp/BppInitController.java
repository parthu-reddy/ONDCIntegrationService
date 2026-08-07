package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;
import com.fooddelivery.ondc.client.PaymentServiceClient;
import com.fooddelivery.ondc.client.RestaurantServiceClient;
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
 * BPP /init endpoint — receives billing and delivery details from BAP.
 * Locks the quote and returns initialization details including payment gateway URI.
 * Returns ACK synchronously, triggers async /on_init callback.
 *
 * CRITICAL: Payment type and status MUST come from PaymentServiceClient — no mocks.
 */
@RestController
public class BppInitController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BppInitController.class);
    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;

    @PostMapping("/init")
    @Transactional
    public ResponseEntity<OndcAckResponse> init(@RequestBody OndcRequest request) {
        log.info("Received /init from BAP: {}, transaction_id: {}", request.getContext().getBapId(), request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());
        OndcTransaction txn = OndcTransaction.builder().transactionId(request.getContext().getTransactionId()).messageId(request.getContext().getMessageId()).action("init").bapId(request.getContext().getBapId()).bppId(request.getContext().getBppId()).state("RECEIVED").build();
        transactionRepository.save(txn);
        // ACK immediately, then dispatch async init processing
        callbackService.processInitAsync(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BppInitController(final OndcSchemaValidator schemaValidator, final OndcTransactionRepository transactionRepository, final BppCallbackService callbackService) {
        this.schemaValidator = schemaValidator;
        this.transactionRepository = transactionRepository;
        this.callbackService = callbackService;
    }
}
