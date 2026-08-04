package com.fooddelivery.ondc.settlement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Maps internal ledger entries to ONDC settlement schema and vice-versa.
 */
@Component
@Slf4j
public class SettlementMapper {
    /**
     * Maps an incoming ONDC recon order to an internal settlement record.
     */
    public java.util.Map<String, Object> mapReconOrderToInternal(java.util.Map<String, Object> reconOrder) {
        if (reconOrder == null) {
            throw new IllegalArgumentException("Reconciliation order cannot be null");
        }
        
        java.util.Map<String, Object> internalRecord = new java.util.HashMap<>();
        internalRecord.put("orderId", reconOrder.get("id"));
        internalRecord.put("amount", reconOrder.get("amount"));
        internalRecord.put("status", reconOrder.get("status"));
        internalRecord.put("settlementId", reconOrder.get("settlement_id"));
        
        return internalRecord;
    }

    /**
     * Maps an internal discrepancy to an ONDC /on_recon order response.
     */
    public java.util.Map<String, Object> mapInternalToReconResponse(java.util.Map<String, Object> internalRecord, BigDecimal diff) {
        java.util.Map<String, Object> responseOrder = new java.util.HashMap<>();
        responseOrder.put("id", internalRecord.get("orderId"));
        responseOrder.put("amount", internalRecord.get("amount"));
        
        if (diff.compareTo(BigDecimal.ZERO) == 0) {
            responseOrder.put("recon_status", "01"); // Reconciled
        } else {
            responseOrder.put("recon_status", "02"); // Not Reconciled
            responseOrder.put("diff_amount", java.util.Map.of("value", diff.toString(), "currency", "INR"));
        }
        
        return responseOrder;
    }
}
