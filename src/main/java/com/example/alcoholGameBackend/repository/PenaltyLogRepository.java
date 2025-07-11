package com.example.alcoholGameBackend.repository;

import com.example.alcoholGameBackend.entity.PenaltyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyLogRepository extends JpaRepository<PenaltyLog, UUID> {
    
    Optional<PenaltyLog> findByDrawResultId(UUID drawResultId);
}