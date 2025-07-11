package com.example.alcoholGameBackend.service;

import com.example.alcoholGameBackend.dto.DrawPenaltyRequest;
import com.example.alcoholGameBackend.dto.DrawPenaltyResponse;
import com.example.alcoholGameBackend.dto.DrawResultResponse;
import com.example.alcoholGameBackend.entity.PenaltyLog;
import com.example.alcoholGameBackend.entity.Player;
import com.example.alcoholGameBackend.entity.Room;
import com.example.alcoholGameBackend.repository.PenaltyLogRepository;
import com.example.alcoholGameBackend.repository.PlayerRepository;
import com.example.alcoholGameBackend.repository.RoomRepository;
import com.example.alcoholGameBackend.websocket.WebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PenaltyService {

    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;
    private final PenaltyLogRepository penaltyLogRepository;
    private final WebSocketController webSocketController;
    private final PushNotificationService pushNotificationService;
    private final Random random = new Random();

    private static final String[] PENALTY_CONTENTS = {
            "술 한 잔 원샷하기",
            "노래 한 곡 부르기",
            "애교 부리기",
            "방 한 바퀴 돌기",
            "손가락으로 머리 위에 하트 그리기",
            "좋아하는 사람 이름 말하기",
            "춤추기 30초",
            "10초 동안 플랭크 자세 유지하기",
            "팔굽혀펴기 10개",
            "얼굴에 화장지 붙이고 떨어뜨리기",
            "5분간 말하지 않기",
            "다른 사람이 시키는 포즈 하기",
            "자신의 장점 3개 말하기",
            "그동안 숨겨온 비밀 하나 말하기",
            "미션 패스권 (다음 벌칙 면제)"
    };

    public DrawPenaltyResponse drawPenalty(UUID roomId, DrawPenaltyRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        List<Player> players = playerRepository.findByRoomIdOrderByPenaltyCountDesc(roomId);
        
        if (players.isEmpty()) {
            throw new RuntimeException("방에 참가자가 없습니다.");
        }

        Player winner = selectRandomPlayer(players);
        String penaltyContent = PENALTY_CONTENTS[random.nextInt(PENALTY_CONTENTS.length)];

        PenaltyLog penaltyLog = new PenaltyLog();
        penaltyLog.setRoom(room);
        penaltyLog.setPlayer(winner);
        penaltyLog.setPenaltyContent(penaltyContent);
        
        PenaltyLog savedLog = penaltyLogRepository.save(penaltyLog);

        winner.setPenaltyCount(winner.getPenaltyCount() + 1);
        playerRepository.save(winner);

        webSocketController.notifyPenaltyDrawn(roomId, winner.getNickname(), penaltyContent);
        pushNotificationService.sendPenaltyNotification(roomId, winner.getNickname());

        return new DrawPenaltyResponse(savedLog.getDrawResultId());
    }

    @Transactional(readOnly = true)
    public DrawResultResponse getDrawResult(UUID drawResultId) {
        PenaltyLog penaltyLog = penaltyLogRepository.findByDrawResultId(drawResultId)
                .orElseThrow(() -> new RuntimeException("뽑기 결과를 찾을 수 없습니다."));

        return new DrawResultResponse(
                penaltyLog.getPlayer().getNickname(),
                penaltyLog.getPenaltyContent()
        );
    }

    private Player selectRandomPlayer(List<Player> players) {
        return players.get(random.nextInt(players.size()));
    }
}