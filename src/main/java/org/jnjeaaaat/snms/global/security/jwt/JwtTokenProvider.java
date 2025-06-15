package org.jnjeaaaat.snms.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.jnjeaaaat.snms.domain.auth.repository.RedisTokenRepository;
import org.jnjeaaaat.snms.global.security.jwt.exception.TokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jnjeaaaat.snms.global.exception.ErrorCode.*;

@Slf4j
@Component
public class JwtTokenProvider {
    private final RedisTokenRepository redisTokenRepository;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(7).toMillis();
    private static final String KEY_ROLE = "role";


    private final SecretKey key;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String key,
            @Value("${jwt.issuer}") String issuer,
            RedisTokenRepository redisTokenRepository) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(UTF_8));
        this.issuer = issuer;
        this.redisTokenRepository = redisTokenRepository;
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

    public boolean validateToken(String token) {
        checkExistsToken(token);

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    public String reissueToken(String accessToken) {
        checkExistsToken(accessToken);

        RedisToken redisToken = redisTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(EXPIRED_TOKEN));

        String refreshToken = redisToken.getRefreshToken();
        if (!validateToken(refreshToken)) {
            throw new TokenException(EXPIRED_TOKEN);
        }

        String reissueAccessToken = createAccessToken(getAuthentication(refreshToken));
        redisToken.updateAccessToken(reissueAccessToken);
        redisTokenRepository.save(redisToken);

        return reissueAccessToken;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        User user = new User(
                claims.getSubject(),
                "",
                Collections.singleton(
                        new SimpleGrantedAuthority(
                                claims.get(KEY_ROLE).toString()
                        )
                ));

        return new UsernamePasswordAuthenticationToken(
                user,
                token,
                user.getAuthorities()
        );
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new TokenException(INVALID_TOKEN);
        }
    }

    private void checkExistsToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new TokenException(EMPTY_TOKEN);
        }
    }
}
