package com.fooddelivery.ondc.repository;

import com.fooddelivery.ondc.entity.OndcTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OndcTransactionRepository extends JpaRepository<OndcTransaction, UUID> {
    Optional<OndcTransaction> findByTransactionIdAndMessageId(String transactionId, String messageId);
    List<OndcTransaction> findByTransactionIdOrderByCreatedAtAsc(String transactionId);
    Optional<OndcTransaction> findByTransactionIdAndAction(String transactionId, String action);
    boolean existsByTransactionIdAndMessageId(String transactionId, String messageId);
}
