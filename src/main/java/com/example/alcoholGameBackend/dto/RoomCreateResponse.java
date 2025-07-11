package com.example.alcoholGameBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RoomCreateResponse {
    private UUID roomId;
    private String inviteLink;
}