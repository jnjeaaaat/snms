package org.jnjeaaaat.snms.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(7).toMillis();
    private static final String KEY_ROLE = "role";


    private final SecretKey key;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String key,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(UTF_8));
        this.issuer = issuer;
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime);

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(KEY_ROLE, authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
}
