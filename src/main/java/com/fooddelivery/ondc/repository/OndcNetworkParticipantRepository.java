package com.fooddelivery.ondc.repository;

import com.fooddelivery.ondc.entity.OndcNetworkParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OndcNetworkParticipantRepository extends JpaRepository<OndcNetworkParticipant, UUID> {
    Optional<OndcNetworkParticipant> findBySubscriberId(String subscriberId);
}
