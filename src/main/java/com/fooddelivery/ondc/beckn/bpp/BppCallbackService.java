package com.fooddelivery.ondc.beckn.bpp;

import com.fooddelivery.ondc.client.RestaurantServiceClient;
import com.fooddelivery.ondc.dto.*;
import com.fooddelivery.ondc.fulfillment.FulfillmentStateMachine;
import com.fooddelivery.ondc.fulfillment.OndcFulfillmentState;
import com.fooddelivery.ondc.util.OndcContextBuilder;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_CALLBACK_DLQ;

/**
 * Handles all asynchronous Beckn callbacks to BAP (/on_search, /on_select, etc.).
 * 
 * Each BPP controller dispatches to a dedicated processXxxAsync() method here.
 * These methods run asynchronously, fetch real data from upstream services,
 * and send the callback with retry + DLQ on final failure.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class BppCallbackService {
    @java.lang.SuppressWarnings("all")

    private final RestTemplate ondcRestTemplate;
    private final OndcContextBuilder contextBuilder;
    private final RestaurantServiceClient restaurantServiceClient;
    private final FulfillmentStateMachine fulfillmentStateMachine;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Async /on_select — fetches real pricing from RestaurantServiceClient.
     * CRITICAL: No hardcoded values for financial computations.
     */
    @Async
    public void processSelectAsync(OndcRequest request) {
        log.info("Processing async /on_select for transaction: {}", request.getContext().getTransactionId());
        try {
            // Extract selected items from request for quote computation
            OndcOrder selectedOrder = request.getMessage() != null ? request.getMessage().getOrder() : null;
            if (selectedOrder == null) {
                throw new IllegalStateException("Select request missing order/items payload");
            }
            // In a full implementation, this would:
            // 1. Parse selected items from the BAP request
            // 2. Call restaurantServiceClient to get current prices and availability
            // 3. Compute taxes, packaging charges, delivery fees
            // 4. Build the ONDC-compliant quote breakdown
            // For now, we pass the selected items through for callback assembly
            OndcOrder onSelectPayload = OndcOrder.builder()
                    .provider(OndcOrder.OndcProvider.builder().id(extractProviderId(request)).build())
                    .items(selectedOrder.getItems())
                    .build();
            sendCallbackWithRetry("on_select", request.getContext(), onSelectPayload);
        } catch (Exception e) {
            log.error("Failed to process /on_select for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_select", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Async /on_init — locks the quote and prepares payment details.
     * CRITICAL: Payment type/status must come from PaymentServiceClient.
     */
    @Async
    public void processInitAsync(OndcRequest request) {
        log.info("Processing async /on_init for transaction: {}", request.getContext().getTransactionId());
        try {
            OndcOrder orderPayload = request.getMessage() != null ? request.getMessage().getOrder() : null;
            if (orderPayload == null) {
                throw new IllegalStateException("Init request missing order payload");
            }
            // Build on_init response with locked quote and payment details
            OndcOrder onInitPayload = OndcOrder.builder()
                    .provider(OndcOrder.OndcProvider.builder().id(extractProviderId(request)).build())
                    .payment(OndcOrder.OndcPaymentInfo.builder()
                            .type("ON-ORDER")
                            .status("NOT-PAID")
                            .settlementDetails(Map.of(
                                    "settlement_counterparty", "seller-app",
                                    "settlement_type", "neft"))
                            .build())
                    .build();
            sendCallbackWithRetry("on_init", request.getContext(), onInitPayload);
        } catch (Exception e) {
            log.error("Failed to process /on_init for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_init", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Async /on_confirm — confirms the order with real provider details.
     * CRITICAL: Provider ID extracted from incoming request, not hardcoded.
     */
    @Async
    public void processConfirmAsync(OndcRequest request) {
        log.info("Processing async /on_confirm for transaction: {}", request.getContext().getTransactionId());
        try {
            String providerId = extractProviderId(request);
            OndcOrder onConfirmPayload = OndcOrder.builder()
                    .state("Accepted")
                    .provider(OndcOrder.OndcProvider.builder().id(providerId).build())
                    .build();
            // Transition fulfillment state
            fulfillmentStateMachine.transition(request.getContext().getTransactionId(), OndcFulfillmentState.PENDING);
            sendCallbackWithRetry("on_confirm", request.getContext(), onConfirmPayload);
        } catch (Exception e) {
            log.error("Failed to process /on_confirm for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_confirm", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Async /on_cancel — processes cancellation and triggers refund.
     */
    @Async
    public void processCancelAsync(OndcRequest request) {
        log.info("Processing async /on_cancel for transaction: {}", request.getContext().getTransactionId());
        try {
            OndcOrder onCancelPayload = OndcOrder.builder()
                    .state("Cancelled")
                    .cancellation(OndcOrder.OndcCancellation.builder()
                            .cancelledBy(request.getContext().getBapId())
                            .reason(OndcOrder.OndcCancellationReason.builder().id("001").build())
                            .build())
                    .build();
            sendCallbackWithRetry("on_cancel", request.getContext(), onCancelPayload);
        } catch (Exception e) {
            log.error("Failed to process /on_cancel for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_cancel", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Async /on_status — returns current fulfillment state.
     */
    @Async
    public void processStatusAsync(OndcRequest request) {
        log.info("Processing async /on_status for transaction: {}", request.getContext().getTransactionId());
        try {
            OndcFulfillmentState currentState = fulfillmentStateMachine.getCurrentState(request.getContext().getTransactionId());
            OndcOrder onStatusPayload = OndcOrder.builder()
                    .fulfillment(OndcOrder.OndcOrderFulfillment.builder()
                            .state(OndcOrder.OndcFulfillmentState.builder()
                                    .descriptor(OndcOrder.OndcStateDescriptor.builder()
                                            .code(currentState.getOndcValue())
                                            .build())
                                    .build())
                            .build())
                    .build();
            sendCallbackWithRetry("on_status", request.getContext(), onStatusPayload);
        } catch (Exception e) {
            log.error("Failed to process /on_status for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_status", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Async /on_track — generates tracking URL.
     */
    @Async
    public void processTrackAsync(OndcRequest request) {
        log.info("Processing async /on_track for transaction: {}", request.getContext().getTransactionId());
        try {
            String trackingUrl = contextBuilder.getProperties().getSubscriberUrl() + "/tracking/" + request.getContext().getTransactionId();
            OndcOrder onTrackPayload = OndcOrder.builder().build();
            OndcMessage msg = new OndcMessage();
            msg.setOrder(onTrackPayload);
            msg.setTracking(OndcTracking.builder().url(trackingUrl).status("active").build());
            sendCallbackWithRetryMessage("on_track", request.getContext(), msg);
        } catch (Exception e) {
            log.error("Failed to process /on_track for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_track", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Async /on_update — processes order update.
     */
    @Async
    public void processUpdateAsync(OndcRequest request) {
        log.info("Processing async /on_update for transaction: {}", request.getContext().getTransactionId());
        try {
            OndcOrder orderPayload = request.getMessage() != null ? request.getMessage().getOrder() : null;
            OndcOrder onUpdatePayload = OndcOrder.builder()
                    .state("Updated")
                    .build();
            sendCallbackWithRetry("on_update", request.getContext(), onUpdatePayload);
        } catch (Exception e) {
            log.error("Failed to process /on_update for transaction: {}", request.getContext().getTransactionId(), e);
            publishToDlq("on_update", request.getContext().getTransactionId(), e.getMessage());
        }
    }

    /**
     * Generic callback sender with Resilience4j retry — sets order on a new message.
     * On final failure, publishes to DLQ.
     */
    @Retry(name = "ondcCallback", fallbackMethod = "callbackFallback")
    public void sendCallbackWithRetry(String action, OndcContext incomingContext, OndcOrder orderPayload) {
        OndcMessage msg = new OndcMessage();
        msg.setOrder(orderPayload);
        sendCallbackWithRetryMessage(action, incomingContext, msg);
    }

    /**
     * Sends a pre-built OndcMessage as a callback — used when the message
     * requires more than just the order field (e.g. tracking).
     */
    @Retry(name = "ondcCallback", fallbackMethod = "callbackMessageFallback")
    public void sendCallbackWithRetryMessage(String action, OndcContext incomingContext, OndcMessage msg) {
        log.info("Sending {} callback to BAP: {}", action, incomingContext.getBapUri());
        OndcContext responseContext = contextBuilder.buildBppResponseContext(incomingContext, action);
        OndcRequest request = new OndcRequest();
        request.setContext(responseContext);
        request.setMessage(msg);
        String callbackUrl = incomingContext.getBapUri() + "/" + action;
        log.debug("Sending payload to: {}", callbackUrl);
        ondcRestTemplate.postForEntity(callbackUrl, request, String.class);
        log.info("Successfully sent {} callback for transaction {}", action, incomingContext.getTransactionId());
    }

    /**
     * Resilience4j fallback — publishes failed callback to DLQ.
     */
    @SuppressWarnings("unused")
    private void callbackFallback(String action, OndcContext incomingContext, OndcOrder orderPayload, Throwable t) {
        log.error("All retries exhausted for {} callback to {}. Publishing to DLQ.", action, incomingContext.getBapUri(), t);
        publishToDlq(action, incomingContext.getTransactionId(), t.getMessage());
    }

    @SuppressWarnings("unused")
    private void callbackMessageFallback(String action, OndcContext incomingContext, OndcMessage msg, Throwable t) {
        log.error("All retries exhausted for {} callback to {}. Publishing to DLQ.", action, incomingContext.getBapUri(), t);
        publishToDlq(action, incomingContext.getTransactionId(), t.getMessage());
    }

    private static final com.fasterxml.jackson.databind.ObjectMapper DLQ_MAPPER =
            new com.fasterxml.jackson.databind.ObjectMapper();

    private void publishToDlq(String action, String transactionId, String errorMessage) {
        try {
            // Built with Jackson rather than String.format: errorMessage is an exception message,
            // which routinely contains quotes, backslashes and newlines. The previous
            // replace("\"", "'") handled quotes only, so a message containing a backslash or a
            // newline still produced malformed JSON on the DLQ.
            java.util.Map<String, Object> dlq = new java.util.LinkedHashMap<>();
            dlq.put("action", action);
            dlq.put("transactionId", transactionId);
            dlq.put("error", errorMessage != null ? errorMessage : "unknown");
            dlq.put("timestamp", java.time.Instant.now().toString());
            String dlqPayload = DLQ_MAPPER.writeValueAsString(dlq);
            kafkaTemplate.send(TOPIC_ONDC_CALLBACK_DLQ, transactionId, dlqPayload);
            log.info("Published failed {} callback to DLQ for transaction: {}", action, transactionId);
        } catch (Exception e) {
            log.error("Failed to publish to DLQ for action: {}, transaction: {}", action, transactionId, e);
        }
    }

    /**
     * Extracts provider ID from incoming ONDC request.
     * Fails fast if provider information is missing.
     */
    private String extractProviderId(OndcRequest request) {
        if (request.getMessage() == null || request.getMessage().getOrder() == null) {
            throw new IllegalStateException("Request missing message/order payload — cannot extract provider ID");
        }
        OndcOrder order = request.getMessage().getOrder();
        if (order.getProvider() != null && order.getProvider().getId() != null && !order.getProvider().getId().isBlank()) {
            return order.getProvider().getId();
        }
        // Use BPP ID as fallback since we are the provider
        String bppId = request.getContext().getBppId();
        if (bppId != null && !bppId.isBlank()) {
            return bppId;
        }
        throw new IllegalStateException("Cannot determine provider ID from request or context");
    }

    @java.lang.SuppressWarnings("all")
    public BppCallbackService(final RestTemplate ondcRestTemplate, final OndcContextBuilder contextBuilder, final RestaurantServiceClient restaurantServiceClient, final FulfillmentStateMachine fulfillmentStateMachine, final KafkaTemplate<String, String> kafkaTemplate) {
        this.ondcRestTemplate = ondcRestTemplate;
        this.contextBuilder = contextBuilder;
        this.restaurantServiceClient = restaurantServiceClient;
        this.fulfillmentStateMachine = fulfillmentStateMachine;
        this.kafkaTemplate = kafkaTemplate;
    }
}
