package org.jnjeaaaat.global.config;

import org.jnjeaaaat.global.security.handler.CustomAccessDeniedHandler;
import org.jnjeaaaat.global.security.handler.CustomAuthenticationEntryPoint;
import org.jnjeaaaat.global.security.jwt.JwtAuthenticationFilter;
import org.jnjeaaaat.global.security.jwt.JwtFailureFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

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

                        .requestMatchers(GET, "/api/posts/**").permitAll()
                        .requestMatchers(POST, "/api/posts").authenticated()

                        .requestMatchers("/client/**").permitAll()

                        .anyRequest().authenticated()
                );

    }
}
