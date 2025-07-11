package com.example.alcoholGameBackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KickPlayerRequest {
    private String hostSessionId;
    private UUID playerId;
}