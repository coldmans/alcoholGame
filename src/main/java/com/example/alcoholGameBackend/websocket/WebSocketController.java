package com.example.alcoholGameBackend.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/room/{roomId}/chat")
    public void handleChatMessage(@DestinationVariable String roomId, @Payload Map<String, Object> message) {
        log.info("방 {}에서 채팅 메시지 수신: {}", roomId, message);
        
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/chat",
                message
        );
    }

    public void notifyPlayerJoined(UUID roomId, String nickname) {
        Map<String, Object> message = Map.of(
                "type", "PLAYER_JOINED",
                "nickname", nickname,
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/players",
                message
        );
    }

    public void notifyPlayerLeft(UUID roomId, String nickname) {
        Map<String, Object> message = Map.of(
                "type", "PLAYER_LEFT",
                "nickname", nickname,
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/players",
                message
        );
    }

    public void notifyPenaltyDrawn(UUID roomId, String winnerNickname, String penaltyContent) {
        Map<String, Object> message = Map.of(
                "type", "PENALTY_DRAWN",
                "winnerNickname", winnerNickname,
                "penaltyContent", penaltyContent,
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/penaltyDrawn",
                message
        );
    }

    public void notifyPlayerKicked(UUID roomId, String kickedNickname) {
        Map<String, Object> message = Map.of(
                "type", "PLAYER_KICKED",
                "nickname", kickedNickname,
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/players",
                message
        );
    }
}