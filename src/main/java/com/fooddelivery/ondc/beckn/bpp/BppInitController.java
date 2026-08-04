package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.ondc.client.PaymentServiceClient;
import com.fooddelivery.ondc.client.RestaurantServiceClient;
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
 * BPP /init endpoint — receives billing and delivery details from BAP.
 * Locks the quote and returns initialization details including payment gateway URI.
 * Returns ACK synchronously, triggers async /on_init callback.
 *
 * CRITICAL: Payment type and status MUST come from PaymentServiceClient — no mocks.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class BppInitController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;

    @PostMapping("/init")
    @Transactional
    public ResponseEntity<OndcAckResponse> init(@RequestBody OndcRequest request) {
        log.info("Received /init from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());

        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("init")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        // ACK immediately, then dispatch async init processing
        callbackService.processInitAsync(request);

        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
