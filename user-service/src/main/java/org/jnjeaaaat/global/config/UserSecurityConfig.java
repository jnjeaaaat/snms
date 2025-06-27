package org.jnjeaaaat.global.config;

import org.jnjeaaaat.global.security.handler.CustomAccessDeniedHandler;
import org.jnjeaaaat.global.security.handler.CustomAuthenticationEntryPoint;
import org.jnjeaaaat.global.security.jwt.JwtAuthenticationFilter;
import org.jnjeaaaat.global.security.jwt.JwtFailureFilter;
import org.jnjeaaaat.global.security.oauth.handler.CustomOAuth2FailureHandler;
import org.jnjeaaaat.global.security.oauth.handler.CustomOAuth2SuccessHandler;
import org.jnjeaaaat.global.security.oauth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class UserSecurityConfig extends BaseSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

    public UserSecurityConfig(CustomAccessDeniedHandler customAccessDeniedHandler,
                              CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                              JwtAuthenticationFilter jwtAuthenticationFilter,
                              JwtFailureFilter jwtFailureFilter,
                              CustomOAuth2UserService customOAuth2UserService,
                              CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                              CustomOAuth2FailureHandler customOAuth2FailureHandler) {
        super(customAccessDeniedHandler, customAuthenticationEntryPoint, jwtAuthenticationFilter, jwtFailureFilter);

        this.customOAuth2UserService = customOAuth2UserService;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {

        http
                .cors(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo ->
                                        userInfo.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                                .failureHandler(customOAuth2FailureHandler)
                )

                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/", "/docs/**", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        .requestMatchers("/client/**").permitAll()

                        .anyRequest().authenticated()
                );
    }
}
