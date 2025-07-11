package com.example.alcoholGameBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RoomInfoResponse {
    private UUID roomId;
    private String hostSessionId;
    private String inviteLink;
}