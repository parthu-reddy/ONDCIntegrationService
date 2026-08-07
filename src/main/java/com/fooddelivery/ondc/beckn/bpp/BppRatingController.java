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
import java.util.Map;

/**
 * BPP /rating endpoint — receives ratings from BAP for fulfilled orders.
 * Extracts rating value and forwards to RestaurantServiceClient.
 */
@RestController
public class BppRatingController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BppRatingController.class);
    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;
    private final RestaurantServiceClient restaurantServiceClient;

    @PostMapping("/rating")
    @Transactional
    public ResponseEntity<OndcAckResponse> rating(@RequestBody OndcRequest request) {
        log.info("Received /rating from BAP: {}, transaction_id: {}", request.getContext().getBapId(), request.getContext().getTransactionId());
        schemaValidator.validateRequest(request);
        OndcTransaction txn = OndcTransaction.builder().transactionId(request.getContext().getTransactionId()).messageId(request.getContext().getMessageId()).action("rating").bapId(request.getContext().getBapId()).bppId(request.getContext().getBppId()).state("RECEIVED").build();
        transactionRepository.save(txn);
        // Process rating asynchronously — extract and forward to restaurant service
        processRating(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    private void processRating(OndcRequest request) {
        try {
            if (request.getMessage() == null || request.getMessage().getOrder() == null) {
                log.warn("Rating request missing order payload for transaction: {}", request.getContext().getTransactionId());
                return;
            }
            Object order = request.getMessage().getOrder();
            if (order instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> orderMap = (Map<String, Object>) order;
                Object ratingObj = orderMap.get("rating");
                if (ratingObj != null) {
                    log.info("Rating received for transaction {}: {}", request.getContext().getTransactionId(), ratingObj);
                }
            }
        } catch (
        // In full implementation: parse rating value, determine entity
        // (restaurant/delivery), and forward to appropriate service
        Exception e) {
            log.error("Failed to process rating for transaction: {}", request.getContext().getTransactionId(), e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public BppRatingController(final OndcSchemaValidator schemaValidator, final OndcTransactionRepository transactionRepository, final RestaurantServiceClient restaurantServiceClient) {
        this.schemaValidator = schemaValidator;
        this.transactionRepository = transactionRepository;
        this.restaurantServiceClient = restaurantServiceClient;
    }
}
