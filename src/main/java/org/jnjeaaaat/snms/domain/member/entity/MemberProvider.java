package org.jnjeaaaat.snms.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jnjeaaaat.snms.domain.member.type.LoginType;
import org.jnjeaaaat.snms.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProvider extends BaseEntity {

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

    @Builder
    protected MemberProvider(Member member, String providerName, String providerUserId, String email) {
        this.member = member;
        this.providerName = LoginType.getLoginTypeFromProviderName(
                providerName
        );
        this.providerUserId = providerUserId;
        this.email = email;
    }

}