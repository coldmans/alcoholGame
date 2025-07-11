package com.example.alcoholGameBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerDto {
    private UUID playerId;
    private String nickname;
    private int penaltyCount;
}