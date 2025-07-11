package com.example.alcoholGameBackend.config;

import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;

@Configuration
public class WebPushConfig {

    @Value("${webpush.vapid.public-key:}")
    private String publicKey;

    @Value("${webpush.vapid.private-key:}")
    private String privateKey;

    @Value("${webpush.vapid.subject:mailto:admin@example.com}")
    private String subject;

    @Bean
    @ConditionalOnProperty(name = "webpush.enabled", havingValue = "true", matchIfMissing = true)
    public PushService pushService() throws GeneralSecurityException {
        PushService pushService = new PushService();
        
        if (!publicKey.isEmpty() && !privateKey.isEmpty()) {
            pushService.setPublicKey(publicKey);
            pushService.setPrivateKey(privateKey);
            pushService.setSubject(subject);
        }
        
        return pushService;
    }
}