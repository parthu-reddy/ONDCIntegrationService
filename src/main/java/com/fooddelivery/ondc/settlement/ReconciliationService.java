package com.fooddelivery.ondc.settlement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles ONDC /recon, /on_recon, /receiver_recon, /on_receiver_recon APIs
 * for order-level financial reconciliation with the Settlement Agency.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReconciliationService {

    private final SettlementService settlementService;

    // TODO: Implement reconciliation flows per RSF 2.0 specification
}
