package org.jnjeaaaat.snms.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.jnjeaaaat.snms.domain.member.type.MemberRole;
import org.jnjeaaaat.snms.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    private String password;

    @Column(nullable = false)
    private String phoneNum;

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
    private MemberRole role;

    private LocalDateTime deletedAt;

    @Builder
    protected Member(String uid, String password, String nickname, String phoneNum, String profileImgUrl, MemberRole role) {
        this.uid = uid;
        this.password = password;
        this.nickname = nickname;
        this.phoneNum = phoneNum;
        this.profileImgUrl = profileImgUrl; // default from client
        this.role = role;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void updateProfileImage(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void switchDefaultProfileImg() {
        this.defaultProfileImg = !this.defaultProfileImg;
    }
}
