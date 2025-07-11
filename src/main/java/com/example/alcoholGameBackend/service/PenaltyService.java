package com.example.alcoholGameBackend.service;

import com.example.alcoholGameBackend.dto.DrawPenaltyRequest;
import com.example.alcoholGameBackend.dto.DrawPenaltyResponse;
import com.example.alcoholGameBackend.dto.DrawResultResponse;
import com.example.alcoholGameBackend.dto.PenaltyLogDto;
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
import java.util.stream.Collectors;

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

        Player originalDrawer = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("플레이어를 찾을 수 없습니다."));

        String penaltyContent = PENALTY_CONTENTS[random.nextInt(PENALTY_CONTENTS.length)];
        
        List<Player> allPlayersInRoom = playerRepository.findByRoomIdOrderByPenaltyCountDesc(roomId);
        
        boolean isRandomTarget = random.nextInt(100) < 90;
        Player penaltyTarget;
        
        if (isRandomTarget && allPlayersInRoom.size() > 1) {
            List<Player> otherPlayers = allPlayersInRoom.stream()
                    .filter(p -> !p.getId().equals(originalDrawer.getId()))
                    .toList();
            penaltyTarget = selectRandomPlayer(otherPlayers);
        } else {
            penaltyTarget = originalDrawer;
            isRandomTarget = false;
        }

        PenaltyLog penaltyLog = new PenaltyLog();
        penaltyLog.setRoom(room);
        penaltyLog.setPlayer(penaltyTarget);
        penaltyLog.setOriginalDrawer(originalDrawer);
        penaltyLog.setPenaltyContent(penaltyContent);
        penaltyLog.setRandomTarget(isRandomTarget);
        
        PenaltyLog savedLog = penaltyLogRepository.save(penaltyLog);

        penaltyTarget.setPenaltyCount(penaltyTarget.getPenaltyCount() + 1);
        playerRepository.save(penaltyTarget);

        return new DrawPenaltyResponse(savedLog.getDrawResultId());
    }

    @Transactional(readOnly = true)
    public DrawResultResponse getDrawResult(UUID drawResultId) {
        PenaltyLog penaltyLog = penaltyLogRepository.findByDrawResultId(drawResultId)
                .orElseThrow(() -> new RuntimeException("뽑기 결과를 찾을 수 없습니다."));

        String originalDrawerNickname = penaltyLog.getOriginalDrawer() != null 
                ? penaltyLog.getOriginalDrawer().getNickname() 
                : penaltyLog.getPlayer().getNickname();

        System.out.println("DEBUG - isRandomTarget: " + penaltyLog.isRandomTarget());
        System.out.println("DEBUG - originalDrawerNickname: " + originalDrawerNickname);
        System.out.println("DEBUG - winnerNickname: " + penaltyLog.getPlayer().getNickname());

        return new DrawResultResponse(
                penaltyLog.getPlayer().getNickname(),
                penaltyLog.getPenaltyContent(),
                penaltyLog.isRandomTarget(),
                originalDrawerNickname
        );
    }

    public void notifyPenaltyRevealed(UUID drawResultId) {
        PenaltyLog penaltyLog = penaltyLogRepository.findByDrawResultId(drawResultId)
                .orElseThrow(() -> new RuntimeException("뽑기 결과를 찾을 수 없습니다."));

        UUID roomId = penaltyLog.getRoom().getId();
        String winnerNickname = penaltyLog.getPlayer().getNickname();
        String penaltyContent = penaltyLog.getPenaltyContent();

        webSocketController.notifyPenaltyDrawn(roomId, winnerNickname, penaltyContent);
        pushNotificationService.sendPenaltyNotification(roomId, winnerNickname);
    }

    @Transactional(readOnly = true)
    public List<PenaltyLogDto> getRecentPenalties(UUID roomId) {
        List<PenaltyLog> penaltyLogs = penaltyLogRepository.findRecentPenaltiesByRoomId(roomId);
        
        return penaltyLogs.stream()
                .map(log -> new PenaltyLogDto(
                        log.getId(),
                        log.getPlayer().getNickname(),
                        log.getPenaltyContent(),
                        log.getDrawnAt(),
                        log.isRandomTarget(),
                        log.getOriginalDrawer() != null 
                                ? log.getOriginalDrawer().getNickname() 
                                : log.getPlayer().getNickname()
                ))
                .collect(Collectors.toList());
    }

    private Player selectRandomPlayer(List<Player> players) {
        return players.get(random.nextInt(players.size()));
    }
}