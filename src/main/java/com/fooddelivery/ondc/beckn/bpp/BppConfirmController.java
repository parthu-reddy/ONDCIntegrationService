package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_ORDER_CREATED;

/**
 * BPP /confirm endpoint — receives payment confirmation from BAP.
 * Creates the order and dispatches to kitchen via Kafka.
 * 
 * CRITICAL: Duplicate confirm with same transaction_id is handled idempotently
 * — returns existing order, does NOT create a duplicate.
 * 
 * CRITICAL: Provider ID and order details are extracted from the incoming request,
 * not hardcoded. Financial data comes from the locked quote in the init phase.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class BppConfirmController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BppCallbackService callbackService;
    private final ObjectMapper objectMapper;

    @PostMapping("/confirm")
    @Transactional
    public ResponseEntity<OndcAckResponse> confirm(@RequestBody OndcRequest request) {
        log.info("Received /confirm from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        schemaValidator.validateRequest(request);
        schemaValidator.validateOrderContext(request.getContext());

        // Idempotency check — prevent duplicate order creation
        boolean alreadyConfirmed = transactionRepository.existsByTransactionIdAndMessageId(
                request.getContext().getTransactionId(),
                request.getContext().getMessageId());

        if (alreadyConfirmed) {
            log.warn("Duplicate /confirm detected for transaction_id: {}. Returning existing ACK.",
                    request.getContext().getTransactionId());
            return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
        }

        // Log transaction
        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("confirm")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        // Publish to Kafka for order creation and kitchen dispatch
        kafkaTemplate.send(TOPIC_ONDC_ORDER_CREATED,
                request.getContext().getTransactionId(),
                serializeRequest(request));

        // Dispatch async on_confirm callback — will fetch real provider/order details
        callbackService.processConfirmAsync(request);

        log.info("ACK sent for /confirm. Order dispatched to Kafka. transaction_id: {}",
                request.getContext().getTransactionId());
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    private String serializeRequest(OndcRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize ONDC confirm request", e);
        }
    }
}
