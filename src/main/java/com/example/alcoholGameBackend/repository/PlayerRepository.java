package com.example.alcoholGameBackend.repository;

import com.example.alcoholGameBackend.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    
    List<Player> findByRoomIdOrderByPenaltyCountDesc(UUID roomId);
    
    Optional<Player> findBySessionId(String sessionId);
    
    @Query("SELECT p FROM Player p WHERE p.room.id = :roomId ORDER BY p.penaltyCount DESC")
    List<Player> findRankingByRoomId(@Param("roomId") UUID roomId);
    
    Optional<Player> findByRoomIdAndNickname(UUID roomId, String nickname);
}