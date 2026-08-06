package com.fooddelivery.ondc.settlement;

import com.fooddelivery.ondc.client.LedgerServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReconciliationServiceTest {

    private ReconciliationService reconciliationService;
    private SettlementService settlementService;
    private SettlementMapper settlementMapper;
    private LedgerServiceClient ledgerServiceClient;

    @BeforeEach
    void setUp() {
        settlementService = Mockito.mock(SettlementService.class);
        settlementMapper = Mockito.mock(SettlementMapper.class);
        ledgerServiceClient = Mockito.mock(LedgerServiceClient.class);
        reconciliationService = new ReconciliationService(settlementService, settlementMapper, ledgerServiceClient);
    }

    @Test
    void testProcessReconciliation_Success() {
        // Arrange
        Map<String, Object> reconOrder = Map.of("id", "order-123");
        Map<String, Object> internalRecord = Map.of(
            "orderId", "order-123",
            "amount", Map.of("value", "105.50")
        );
        Map<String, Object> expectedResponse = Map.of("id", "order-123", "diff", 0);

        when(settlementMapper.mapReconOrderToInternal(reconOrder)).thenReturn(internalRecord);
        when(ledgerServiceClient.getOrderLedgerAmount("order-123")).thenReturn(new BigDecimal("105.50"));
        when(settlementMapper.mapInternalToReconResponse(Mockito.eq(internalRecord), Mockito.any(BigDecimal.class))).thenReturn(expectedResponse);

        // Act
        List<Map<String, Object>> result = reconciliationService.processReconciliation(List.of(reconOrder));

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result.get(0));
    }

    @Test
    void testProcessReconciliation_WithDiscrepancy() {
        // Arrange
        Map<String, Object> reconOrder = Map.of("id", "order-456");
        Map<String, Object> internalRecord = Map.of(
            "orderId", "order-456",
            "amount", Map.of("value", "100.00") // Received
        );
        Map<String, Object> expectedResponse = Map.of("id", "order-456", "diff", 5.50);

        when(settlementMapper.mapReconOrderToInternal(reconOrder)).thenReturn(internalRecord);
        when(ledgerServiceClient.getOrderLedgerAmount("order-456")).thenReturn(new BigDecimal("105.50")); // Expected
        when(settlementMapper.mapInternalToReconResponse(Mockito.eq(internalRecord), Mockito.any(BigDecimal.class))).thenReturn(expectedResponse);

        // Act
        List<Map<String, Object>> result = reconciliationService.processReconciliation(List.of(reconOrder));

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result.get(0));
    }

    @Test
    void testProcessReconciliation_LedgerThrowsException() {
        // Arrange
        Map<String, Object> reconOrder = Map.of("id", "order-789");
        Map<String, Object> internalRecord = Map.of("orderId", "order-789");

        when(settlementMapper.mapReconOrderToInternal(reconOrder)).thenReturn(internalRecord);
        when(ledgerServiceClient.getOrderLedgerAmount("order-789")).thenThrow(new RuntimeException("API down"));

        // Act
        List<Map<String, Object>> result = reconciliationService.processReconciliation(List.of(reconOrder));

        // Assert
        // Exception should be caught and logged, resulting in an empty or partially complete list. 
        // In the current implementation, it continues but adds nothing for that failed order.
        assertEquals(0, result.size());
    }
}
