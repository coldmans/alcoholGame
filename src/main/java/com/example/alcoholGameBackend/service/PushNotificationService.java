package com.example.alcoholGameBackend.service;

import com.example.alcoholGameBackend.dto.ApiResponse;
import com.example.alcoholGameBackend.dto.PushSubscriptionRequest;
import com.example.alcoholGameBackend.entity.Player;
import com.example.alcoholGameBackend.entity.PushSubscription;
import com.example.alcoholGameBackend.repository.PlayerRepository;
import com.example.alcoholGameBackend.repository.PushSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@Slf4j
public class PushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PlayerRepository playerRepository;
    
    @Autowired(required = false)
    private PushService pushService;

    public PushNotificationService(PushSubscriptionRepository pushSubscriptionRepository, 
                                   PlayerRepository playerRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.playerRepository = playerRepository;
    }

    public ApiResponse subscribe(PushSubscriptionRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("플레이어를 찾을 수 없습니다."));

        pushSubscriptionRepository.findByPlayerIdAndEndpoint(request.getPlayerId(), request.getEndpoint())
                .ifPresentOrElse(
                        existing -> {
                            existing.setP256dh(request.getP256dh());
                            existing.setAuth(request.getAuth());
                            pushSubscriptionRepository.save(existing);
                        },
                        () -> {
                            PushSubscription subscription = new PushSubscription();
                            subscription.setPlayer(player);
                            subscription.setEndpoint(request.getEndpoint());
                            subscription.setP256dh(request.getP256dh());
                            subscription.setAuth(request.getAuth());
                            pushSubscriptionRepository.save(subscription);
                        }
                );

        return new ApiResponse("success");
    }

    public ApiResponse unsubscribe(UUID playerId, String endpoint) {
        pushSubscriptionRepository.findByPlayerIdAndEndpoint(playerId, endpoint)
                .ifPresent(pushSubscriptionRepository::delete);

        return new ApiResponse("success");
    }

    public void sendPenaltyNotification(UUID roomId, String winnerNickname) {
        if (pushService == null) {
            log.info("푸시 서비스가 비활성화되어 있습니다.");
            return;
        }

        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByPlayerRoomId(roomId);

        String title = "🍻 벌칙 알림!";
        String body = winnerNickname + "님이 벌칙에 당첨되었습니다!";

        subscriptions.forEach(subscription -> {
            CompletableFuture.runAsync(() -> {
                try {
                    Notification notification = new Notification(
                            subscription.getEndpoint(),
                            subscription.getP256dh(),
                            subscription.getAuth(),
                            String.format("{\"title\":\"%s\",\"body\":\"%s\",\"icon\":\"/icon-192x192.png\"}", title, body)
                    );

                    pushService.send(notification);
                    log.info("푸시 알림 전송 성공: {}", subscription.getEndpoint());
                } catch (Exception e) {
                    log.error("푸시 알림 전송 실패: {}", subscription.getEndpoint(), e);
                }
            });
        });
    }
}