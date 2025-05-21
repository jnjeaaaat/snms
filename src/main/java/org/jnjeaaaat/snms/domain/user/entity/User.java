package org.jnjeaaaat.snms.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.jnjeaaaat.snms.domain.user.type.LoginType;
import org.jnjeaaaat.snms.domain.user.type.UserRole;
import org.jnjeaaaat.snms.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class User extends BaseEntity {

    private static final String DEFAULT_PASSWORD = "qwER12!@";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImgUrl;

    @Column
    private Boolean defaultProfileImg = true;

    @Column
    private String introduce;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private LocalDateTime deletedAt;

    @Builder
    protected User(String email, String password, String nickname, String profileImgUrl, UserRole role, LoginType loginType) {
        this.email = email;
        this.password = password == null ? DEFAULT_PASSWORD : password;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl; // default from client
        this.role = role;
        this.loginType = loginType;
    }
}
