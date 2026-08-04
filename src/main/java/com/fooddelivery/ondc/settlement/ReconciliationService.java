package com.fooddelivery.ondc.settlement;

import com.fooddelivery.ondc.client.LedgerServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Handles ONDC /recon, /on_recon, /receiver_recon, /on_receiver_recon APIs
 * for order-level financial reconciliation with the Settlement Agency.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReconciliationService {

    private final SettlementService settlementService;
    private final SettlementMapper settlementMapper;
    private final LedgerServiceClient ledgerServiceClient;

    /**
     * Processes a /recon request containing a batch of settlement orders from the Settlement Agency.
     */
    public java.util.List<java.util.Map<String, Object>> processReconciliation(java.util.List<java.util.Map<String, Object>> reconOrders) {
        log.info("Processing reconciliation for {} orders", reconOrders != null ? reconOrders.size() : 0);
        
        java.util.List<java.util.Map<String, Object>> onReconResponses = new java.util.ArrayList<>();
        
        if (reconOrders == null) return onReconResponses;

        for (java.util.Map<String, Object> reconOrder : reconOrders) {
            try {
                // Map to internal format
                java.util.Map<String, Object> internalRecord = settlementMapper.mapReconOrderToInternal(reconOrder);
                String orderId = (String) internalRecord.get("orderId");
                
                // Fetch our ledger record via Feign
                BigDecimal expectedAmount = getLedgerAmount(orderId);
                
                // Compare amounts
                Object amountObj = internalRecord.get("amount");
                Map<String, Object> amountMap = null;
                BigDecimal receivedAmount = BigDecimal.ZERO;
                if (amountObj instanceof Map) {
                    amountMap = (Map<String, Object>) amountObj;
                    receivedAmount = new BigDecimal(String.valueOf(amountMap.get("value")));
                } else if (amountObj != null) {
                    receivedAmount = new BigDecimal(String.valueOf(amountObj));
                }

                BigDecimal diff = expectedAmount.subtract(receivedAmount);
                
                // Log discrepancy if any
                if (diff.compareTo(BigDecimal.ZERO) != 0) {
                    log.warn("Discrepancy found for order {}: Expected {}, Received {}, Diff {}", 
                             orderId, expectedAmount, receivedAmount, diff);
                }
                
                // Map to response format
                onReconResponses.add(settlementMapper.mapInternalToReconResponse(internalRecord, diff));
                
            } catch (Exception e) {
                log.error("Failed to process reconciliation for order {}", reconOrder.get("id"), e);
            }
        }
        
        return onReconResponses;
    }
    
    private BigDecimal getLedgerAmount(String orderId) {
        try {
            BigDecimal amount = ledgerServiceClient.getOrderLedgerAmount(orderId);
            if (amount == null) {
                throw new IllegalStateException("LedgerService returned null amount for order " + orderId);
            }
            return amount;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch ledger amount for order " + orderId + " from upstream service", e);
        }
    }
}
