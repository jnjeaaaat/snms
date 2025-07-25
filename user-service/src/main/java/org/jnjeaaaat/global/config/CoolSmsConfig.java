package org.jnjeaaaat.global.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class CoolSmsConfig {

    @Value("${coolsms.key}")
    String apiKey;

    @Value("${coolsms.secret}")
    String secretKey;

    @Bean
    public DefaultMessageService defaultMessageService() {
        return NurigoApp.INSTANCE.initialize(apiKey, secretKey, "https://api.coolsms.co.kr");
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
