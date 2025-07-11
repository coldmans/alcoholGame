package com.example.alcoholGameBackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PushSubscriptionRequest {
    private UUID playerId;
    private String endpoint;
    private String p256dh;
    private String auth;
}