package org.jnjeaaaat.snms.global.config;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.global.security.handler.CustomAccessDeniedHandler;
import org.jnjeaaaat.snms.global.security.handler.CustomAuthenticationEntryPoint;
import org.jnjeaaaat.snms.global.security.jwt.JwtAuthenticationFilter;
import org.jnjeaaaat.snms.global.security.jwt.JwtFailureFilter;
import org.jnjeaaaat.snms.global.security.oauth.handler.CustomOAuth2FailureHandler;
import org.jnjeaaaat.snms.global.security.oauth.handler.CustomOAuth2SuccessHandler;
import org.jnjeaaaat.snms.global.security.oauth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtFailureFilter jwtFailureFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/", "/docs/**", "/error", "/favicon.ico").permitAll()

                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo ->
                                        userInfo.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                                .failureHandler(customOAuth2FailureHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFailureFilter, JwtAuthenticationFilter.class)

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                .build();
    }
}
