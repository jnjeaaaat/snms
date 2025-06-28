package org.jnjeaaaat.global.config;

import org.jnjeaaaat.global.security.handler.CustomAccessDeniedHandler;
import org.jnjeaaaat.global.security.handler.CustomAuthenticationEntryPoint;
import org.jnjeaaaat.global.security.jwt.JwtAuthenticationFilter;
import org.jnjeaaaat.global.security.jwt.JwtFailureFilter;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class PostSecurityConfig extends BaseSecurityConfig {
    public PostSecurityConfig(CustomAccessDeniedHandler customAccessDeniedHandler,
                              CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                              JwtAuthenticationFilter jwtAuthenticationFilter,
                              JwtFailureFilter jwtFailureFilter) {
        super(customAccessDeniedHandler, customAuthenticationEntryPoint, jwtAuthenticationFilter, jwtFailureFilter);
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/", "/docs/**", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        .requestMatchers("/client/**").permitAll()

                        .anyRequest().authenticated()
                );

    }
}
