package org.jnjeaaaat.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jnjeaaaat.domain.member.type.LoginType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType providerName;

    @Column(nullable = false)
    private String providerUserId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime lastLoginAt;

    @Builder
    protected MemberProvider(Member member, String providerName, String providerUserId, String email) {
        this.member = member;
        this.providerName = LoginType.getLoginTypeFromProviderName(
                providerName
        );
        this.providerUserId = providerUserId;
        this.email = email;
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

}