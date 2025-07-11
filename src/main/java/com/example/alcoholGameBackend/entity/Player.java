package com.example.alcoholGameBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @Column(name = "nickname", nullable = false)
    private String nickname;
    
    @Column(name = "penalty_count", nullable = false)
    private int penaltyCount = 0;
    
    @Column(name = "last_active_at", nullable = false)
    private LocalDateTime lastActiveAt;
    
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PenaltyLog> penaltyLogs;
    
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PushSubscription> pushSubscriptions;
    
    @PrePersist
    protected void onCreate() {
        lastActiveAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastActiveAt = LocalDateTime.now();
    }
}