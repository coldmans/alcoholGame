package com.example.alcoholGameBackend.repository;

import com.example.alcoholGameBackend.entity.PenaltyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenaltyLogRepository extends JpaRepository<PenaltyLog, UUID> {
    
    Optional<PenaltyLog> findByDrawResultId(UUID drawResultId);
    
    @Query("SELECT p FROM PenaltyLog p WHERE p.room.id = :roomId ORDER BY p.drawnAt DESC")
    List<PenaltyLog> findRecentPenaltiesByRoomId(@Param("roomId") UUID roomId);
}