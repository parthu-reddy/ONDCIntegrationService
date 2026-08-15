package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;
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
 * BPP /select endpoint — receives cart items from BAP.
 * Validates inventory, calculates taxes/charges.
 * Returns ACK synchronously, triggers async /on_select callback with quote breakdown.
 *
 * CRITICAL: Financial quote MUST come from RestaurantServiceClient — no hardcoded values.
 */
@RestController
@lombok.extern.slf4j.Slf4j
public class BppSelectController {
    @java.lang.SuppressWarnings("all")

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;
    private final RestaurantServiceClient restaurantServiceClient;

    @PostMapping("/select")
    @Transactional
    public ResponseEntity<OndcAckResponse> select(@RequestBody OndcRequest request) {
        log.info("Received /select from BAP: {}, transaction_id: {}", request.getContext().getBapId(), request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());
        // Log transaction
        OndcTransaction txn = OndcTransaction.builder().transactionId(request.getContext().getTransactionId()).messageId(request.getContext().getMessageId()).action("select").bapId(request.getContext().getBapId()).bppId(request.getContext().getBppId()).state("RECEIVED").build();
        transactionRepository.save(txn);
        // ACK immediately, then dispatch async quote computation
        callbackService.processSelectAsync(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public BppSelectController(final OndcSchemaValidator schemaValidator, final OndcTransactionRepository transactionRepository, final BppCallbackService callbackService, final RestaurantServiceClient restaurantServiceClient) {
        this.schemaValidator = schemaValidator;
        this.transactionRepository = transactionRepository;
        this.callbackService = callbackService;
        this.restaurantServiceClient = restaurantServiceClient;
    }
}
