package com.example.alcoholGameBackend.repository;

import com.example.alcoholGameBackend.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {
    
    List<PushSubscription> findByPlayerId(UUID playerId);
    
    Optional<PushSubscription> findByPlayerIdAndEndpoint(UUID playerId, String endpoint);
    
    List<PushSubscription> findByPlayerRoomId(UUID roomId);
}