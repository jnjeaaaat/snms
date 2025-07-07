package org.jnjeaaaat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jnjeaaaat.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(unique = true)
    private String token;

    @Column(nullable = false)
    private String userAgent;

    @Builder
    protected FcmToken(Long memberId, String token, String userAgent) {
        this.memberId = memberId;
        this.token = token;
        this.userAgent = userAgent;
    }

    public void refreshToken(String token) {
        this.token = token;
    }

    public void deleteToken() {
        this.token = null;
    }
}
