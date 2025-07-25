package org.jnjeaaaat.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "org.jnjeaaaat.global.client.member")
@Configuration
public class FeignNotificationConfig {
}
