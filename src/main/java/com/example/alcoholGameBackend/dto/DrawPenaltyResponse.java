package com.example.alcoholGameBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DrawPenaltyResponse {
    private UUID drawResultId;
}