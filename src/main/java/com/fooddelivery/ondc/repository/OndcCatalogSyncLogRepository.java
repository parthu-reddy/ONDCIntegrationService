package com.fooddelivery.ondc.repository;

import com.fooddelivery.ondc.entity.OndcCatalogSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OndcCatalogSyncLogRepository extends JpaRepository<OndcCatalogSyncLog, UUID> {
}
