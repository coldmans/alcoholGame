package com.example.alcoholGameBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class DrawResultResponse {
    private String winnerNickname;
    private String penaltyContent;
    private boolean isRandomTarget;
    private String originalDrawerNickname;
}