package com.example.alcoholGameBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PenaltyLogDto {
    private UUID logId;
    private String winnerNickname;
    private String penaltyContent;
    private LocalDateTime drawnAt;
    private boolean isRandomTarget;
    private String originalDrawerNickname;
}