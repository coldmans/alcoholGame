package com.example.alcoholGameBackend.controller;

import com.example.alcoholGameBackend.dto.*;
import com.example.alcoholGameBackend.service.PenaltyService;
import com.example.alcoholGameBackend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final PenaltyService penaltyService;

    @PostMapping
    public ResponseEntity<RoomCreateResponse> createRoom(@RequestBody RoomCreateRequest request) {
        RoomCreateResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<PlayerJoinResponse> joinRoom(
            @PathVariable UUID roomId,
            @RequestBody PlayerJoinRequest request) {
        PlayerJoinResponse response = roomService.joinRoom(roomId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/players")
    public ResponseEntity<List<PlayerDto>> getPlayers(@PathVariable UUID roomId) {
        List<PlayerDto> players = roomService.getPlayers(roomId);
        return ResponseEntity.ok(players);
    }

    @PostMapping("/{roomId}/drawPenalty")
    public ResponseEntity<DrawPenaltyResponse> drawPenalty(
            @PathVariable UUID roomId,
            @RequestBody DrawPenaltyRequest request) {
        DrawPenaltyResponse response = penaltyService.drawPenalty(roomId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/rankings/penalty")
    public ResponseEntity<List<PlayerDto>> getPenaltyRanking(@PathVariable UUID roomId) {
        List<PlayerDto> ranking = roomService.getRanking(roomId);
        return ResponseEntity.ok(ranking);
    }

    @PostMapping("/{roomId}/kick")
    public ResponseEntity<ApiResponse> kickPlayer(
            @PathVariable UUID roomId,
            @RequestBody KickPlayerRequest request) {
        roomService.kickPlayer(roomId, request);
        return ResponseEntity.ok(new ApiResponse("success"));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(@PathVariable UUID roomId) {
        RoomInfoResponse roomInfo = roomService.getRoomInfo(roomId);
        return ResponseEntity.ok(roomInfo);
    }

    @GetMapping("/{roomId}/penalties/recent")
    public ResponseEntity<List<PenaltyLogDto>> getRecentPenalties(@PathVariable UUID roomId) {
        List<PenaltyLogDto> recentPenalties = penaltyService.getRecentPenalties(roomId);
        return ResponseEntity.ok(recentPenalties);
    }
}