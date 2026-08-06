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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_SEARCH_REQUEST;

/**
 * BPP /search endpoint — receives search intent from BAP via ONDC Gateway.
 * Returns ACK synchronously and triggers async catalog lookup + /on_search callback.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BppSearchController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/search")
    public ResponseEntity<OndcAckResponse> search(@RequestBody OndcRequest request) {
        log.info("Received /search from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        // Validate mandatory fields
        schemaValidator.validateRequest(request);
        schemaValidator.validateSearchContext(request.getContext());

        // Log transaction for audit
        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("search")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        // Publish to Kafka for async processing (catalog lookup + on_search callback)
        kafkaTemplate.send(TOPIC_ONDC_SEARCH_REQUEST,
                request.getContext().getTransactionId(),
                serializeRequest(request));

        log.info("ACK sent for /search, transaction_id: {}", request.getContext().getTransactionId());
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    private String serializeRequest(OndcRequest request) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize ONDC search request", e);
        }
    }
}
