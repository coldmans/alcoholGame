package com.example.alcoholGameBackend.dto;

import lombok.Data;

@Data
public class PlayerJoinRequest {
    private String sessionId;
    private String nickname;
}