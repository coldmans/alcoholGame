package com.example.alcoholGameBackend.controller;

import com.example.alcoholGameBackend.dto.ApiResponse;
import com.example.alcoholGameBackend.dto.PushSubscriptionRequest;
import com.example.alcoholGameBackend.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse> subscribe(@RequestBody PushSubscriptionRequest request) {
        ApiResponse response = pushNotificationService.subscribe(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse> unsubscribe(@RequestBody Map<String, Object> request) {
        UUID playerId = UUID.fromString((String) request.get("playerId"));
        String endpoint = (String) request.get("endpoint");
        
        ApiResponse response = pushNotificationService.unsubscribe(playerId, endpoint);
        return ResponseEntity.ok(response);
    }
}