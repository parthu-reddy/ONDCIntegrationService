package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;

@Component("ondcLedgerServiceClientFallback")
public class LedgerServiceClientFallback implements LedgerServiceClient {
    @Override
    public Map<String, Object> createLedgerEntry(Map<String, Object> ledgerEntry) {
        throw new IllegalStateException("Ledger service is currently unavailable. Failing fast to ensure financial integrity.");
    }

    @Override
    public BigDecimal getOrderLedgerAmount(String orderId) {
        throw new IllegalStateException("Ledger service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}
