package com.fooddelivery.ondc.settlement;

import com.fooddelivery.ondc.client.LedgerServiceClient;
import com.fooddelivery.ondc.client.PaymentServiceClient;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
import com.fooddelivery.ondc.entity.OndcSettlementRecord;
import com.fooddelivery.ondc.repository.OndcSettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Orchestrates ONDC RSF 2.0 settlement flows.
 * Ensures idempotent settlement processing per user financial integrity rules.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SettlementService {

    private final OndcSettlementRepository settlementRepository;
    private final LedgerServiceClient ledgerClient;
    private final PaymentServiceClient paymentClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Processes a settlement event. Idempotent — rejects duplicate settlements.
     *
     * @param transactionId ONDC transaction ID
     * @param settlementType COLLECT, SETTLE, or REFUND
     * @param amount settlement amount (NO default values — fail if null/zero)
     * @param currency currency code
     */
    @Transactional
    public void processSettlement(String transactionId, String settlementType,
                                   BigDecimal amount, String currency) {
        // Financial integrity: NO default values
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Settlement amount must be positive and non-null. " +
                            "Received: " + amount + " for transaction: " + transactionId);
        }

        // Idempotency check — prevent double-credit/debit
        if (settlementRepository.existsByOndcTransactionIdAndSettlementType(
                transactionId, settlementType)) {
            log.warn("Duplicate settlement detected. transaction_id: {}, type: {}. Skipping.",
                    transactionId, settlementType);
            return;
        }

        OndcSettlementRecord record = OndcSettlementRecord.builder()
                .ondcTransactionId(transactionId)
                .settlementType(settlementType)
                .amount(amount)
                .currency(currency)
                .status("PENDING")
                .build();

        settlementRepository.save(record);
        log.info("Settlement record created: transaction={}, type={}, amount={} {}",
                transactionId, settlementType, amount, currency);

        // 1. Ledger Entry
        try {
            Map<String, Object> ledgerEntry = Map.of(
                    "transactionId", transactionId,
                    "direction", "COLLECT".equals(settlementType) ? "CREDIT" : "DEBIT",
                    "amount", amount,
                    "currency", currency,
                    "type", "ONDC_SETTLEMENT"
            );
            ledgerClient.createLedgerEntry(ledgerEntry);
            
            // 2. Broadcast Settlement Event via Kafka
            // We use Jackson in a real app, but for now simple JSON string
            String eventJson = String.format(
                "{\"transactionId\":\"%s\",\"type\":\"%s\",\"amount\":%s,\"currency\":\"%s\"}",
                transactionId, settlementType, amount, currency
            );
            kafkaTemplate.send(OndcKafkaConfig.TOPIC_ONDC_SETTLEMENT_EVENT, transactionId, eventJson);
            
            record.setStatus("COMPLETED");
            settlementRepository.save(record);
        } catch (Exception e) {
            log.error("Failed to process settlement interactions for transaction {}", transactionId, e);
            record.setStatus("FAILED");
            record.setErrorDetails(e.getMessage());
            settlementRepository.save(record);
            throw new IllegalStateException("Settlement integration failed", e);
        }
    }
}
