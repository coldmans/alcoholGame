package com.example.alcoholGameBackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DrawPenaltyRequest {
    private UUID playerId;
}