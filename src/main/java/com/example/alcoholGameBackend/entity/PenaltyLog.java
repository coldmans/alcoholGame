package com.example.alcoholGameBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "penalty_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @Column(name = "penalty_content", nullable = false)
    private String penaltyContent;
    
    @Column(name = "drawn_at", nullable = false)
    private LocalDateTime drawnAt;
    
    @Column(name = "draw_result_id", nullable = false, unique = true)
    private UUID drawResultId;
    
    @PrePersist
    protected void onCreate() {
        drawnAt = LocalDateTime.now();
        if (drawResultId == null) {
            drawResultId = UUID.randomUUID();
        }
    }
}