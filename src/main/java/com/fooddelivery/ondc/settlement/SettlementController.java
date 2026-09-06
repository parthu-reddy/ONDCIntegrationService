package com.fooddelivery.ondc.settlement;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Settlement and reconciliation callback endpoints (RSF 2.0).
 */
@RestController
@lombok.extern.slf4j.Slf4j
public class SettlementController {
    @java.lang.SuppressWarnings("all")

    private final OndcSchemaValidator schemaValidator;
    private final SettlementService settlementService;
    private final ReconciliationService reconciliationService;

    @PostMapping("/settle")
    public ResponseEntity<OndcAckResponse> settle(@RequestBody OndcRequest request) {
        log.info("Received /settle");
        schemaValidator.validateRequest(request);
        CompletableFuture.runAsync(() -> {
            try {
                // Simplified extraction for wiring
                if (request.getMessage() != null && request.getMessage().getSettlement() != null) {
                    com.fooddelivery.ondc.dto.OndcSettlement settlement = request.getMessage().getSettlement();
                    java.util.List<com.fooddelivery.ondc.dto.OndcSettlement.SettlementEntry> settlements = settlement.getSettlements();
                    if (settlements != null && !settlements.isEmpty()) {
                        for (com.fooddelivery.ondc.dto.OndcSettlement.SettlementEntry s : settlements) {
                            String type = s.getSettlementType();
                            com.fooddelivery.ondc.dto.OndcSettlement.OndcAmount amountObj = s.getAmount();
                            if (amountObj != null) {
                                BigDecimal amount = new BigDecimal(amountObj.getValue());
                                String currency = amountObj.getCurrency();
                                settlementService.processSettlement(request.getContext().getTransactionId(), type, amount, currency);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing /settle asynchronously", e);
            }
        });
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/on_settle")
    public ResponseEntity<OndcAckResponse> onSettle(@RequestBody OndcRequest request) {
        log.info("Received /on_settle");
        schemaValidator.validateRequest(request);
        // Note: For BPP, on_settle is typically received from BAP.
        // We log it and optionally update settlement status in SettlementService.
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/recon")
    public ResponseEntity<OndcAckResponse> recon(@RequestBody OndcRequest request) {
        log.info("Received /recon");
        schemaValidator.validateRequest(request);
        CompletableFuture.runAsync(() -> {
            try {
                if (request.getMessage() != null && request.getMessage().getOrder() != null) {
                    com.fooddelivery.ondc.dto.OndcOrder orderObj = request.getMessage().getOrder();
                    java.util.List<java.util.Map<String, Object>> orders = orderObj.getOrders();
                    if (orders != null && !orders.isEmpty()) {
                        List<Map<String, Object>> responses = reconciliationService.processReconciliation(orders);
                        // In a real implementation, we would send these responses back via /on_recon
                        log.info("Reconciliation processed. Computed {} responses.", responses.size());
                    }
                }
            } catch (Exception e) {
                log.error("Error processing /recon asynchronously", e);
            }
        });
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/on_recon")
    public ResponseEntity<OndcAckResponse> onRecon(@RequestBody OndcRequest request) {
        log.info("Received /on_recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/receiver_recon")
    public ResponseEntity<OndcAckResponse> receiverRecon(@RequestBody OndcRequest request) {
        log.info("Received /receiver_recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @PostMapping("/on_receiver_recon")
    public ResponseEntity<OndcAckResponse> onReceiverRecon(@RequestBody OndcRequest request) {
        log.info("Received /on_receiver_recon");
        schemaValidator.validateRequest(request);
        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }

    @java.lang.SuppressWarnings("all")
    public SettlementController(final OndcSchemaValidator schemaValidator, final SettlementService settlementService, final ReconciliationService reconciliationService) {
        this.schemaValidator = schemaValidator;
        this.settlementService = settlementService;
        this.reconciliationService = reconciliationService;
    }
}
