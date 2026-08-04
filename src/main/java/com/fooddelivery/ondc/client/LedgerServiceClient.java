package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client for LedgerService (Eureka: ledger-service).
 * Used for settlement reconciliation and financial auditing.
 * 
 * CRITICAL: All ledger entries must be idempotent. The LedgerService
 * uses unique constraints on transaction_id + direction to prevent duplicates.
 */
@FeignClient(name = "ledger-service")
public interface LedgerServiceClient {

    @PostMapping("/api/v1/ledger/entries")
    Map<String, Object> createLedgerEntry(@RequestBody Map<String, Object> ledgerEntry);
}
