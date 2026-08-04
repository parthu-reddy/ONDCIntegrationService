package com.fooddelivery.ondc.repository;

import com.fooddelivery.ondc.entity.OndcSettlementRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OndcSettlementRepository extends JpaRepository<OndcSettlementRecord, UUID> {
    Optional<OndcSettlementRecord> findByOndcTransactionIdAndSettlementType(String ondcTransactionId, String settlementType);
    boolean existsByOndcTransactionIdAndSettlementType(String ondcTransactionId, String settlementType);
}
