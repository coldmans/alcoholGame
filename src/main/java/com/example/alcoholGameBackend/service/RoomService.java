package com.example.alcoholGameBackend.service;

import com.example.alcoholGameBackend.dto.*;
import com.example.alcoholGameBackend.dto.KickPlayerRequest;
import com.example.alcoholGameBackend.entity.Player;
import com.example.alcoholGameBackend.entity.Room;
import com.example.alcoholGameBackend.repository.PlayerRepository;
import com.example.alcoholGameBackend.repository.RoomRepository;
import com.example.alcoholGameBackend.websocket.WebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final WebSocketController webSocketController;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public RoomCreateResponse createRoom(RoomCreateRequest request) {
        Room room = new Room();
        room.setHostSessionId(request.getHostSessionId());
        
        Room savedRoom = roomRepository.save(room);
        String inviteLink = frontendUrl + "/join?roomId=" + savedRoom.getId();
        
        return new RoomCreateResponse(savedRoom.getId(), inviteLink);
    }

    public PlayerJoinResponse joinRoom(UUID roomId, PlayerJoinRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        Optional<Player> existingPlayer = playerRepository.findByRoomIdAndNickname(roomId, request.getNickname());
        
        Player player;
        if (existingPlayer.isPresent()) {
            player = existingPlayer.get();
            player.setSessionId(request.getSessionId());
        } else {
            player = new Player();
            player.setRoom(room);
            player.setSessionId(request.getSessionId());
            player.setNickname(request.getNickname());
        }
        
        Player savedPlayer = playerRepository.save(player);
        
        webSocketController.notifyPlayerJoined(room.getId(), savedPlayer.getNickname());
        
        return new PlayerJoinResponse(
                savedPlayer.getId(),
                savedPlayer.getNickname(),
                room.getId()
        );
    }

    @Transactional(readOnly = true)
    public List<PlayerDto> getPlayers(UUID roomId) {
        List<Player> players = playerRepository.findByRoomIdOrderByPenaltyCountDesc(roomId);
        
        return players.stream()
                .map(player -> new PlayerDto(
                        player.getId(),
                        player.getNickname(),
                        player.getPenaltyCount()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerDto> getRanking(UUID roomId) {
        List<Player> players = playerRepository.findRankingByRoomId(roomId);
        
        return players.stream()
                .map(player -> new PlayerDto(
                        player.getId(),
                        player.getNickname(),
                        player.getPenaltyCount()
                ))
                .collect(Collectors.toList());
    }

    public void kickPlayer(UUID roomId, KickPlayerRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        if (!room.getHostSessionId().equals(request.getHostSessionId())) {
            throw new RuntimeException("방장만 강퇴할 수 있습니다.");
        }

        Player playerToKick = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("플레이어를 찾을 수 없습니다."));

        if (!playerToKick.getRoom().getId().equals(roomId)) {
            throw new RuntimeException("해당 방의 플레이어가 아닙니다.");
        }

        String kickedNickname = playerToKick.getNickname();
        playerRepository.delete(playerToKick);

        webSocketController.notifyPlayerKicked(roomId, kickedNickname);
    }
}