package org.jnjeaaaat.domain.member.dto.response;

import lombok.Builder;
import org.jnjeaaaat.domain.member.entity.Member;

import java.time.LocalDateTime;

@Builder
public record UpdateMemberResponse(
        Long id,
        String nickname,
        String profileImgUrl,
        Boolean defaultProfileImg,
        String introduce,
        LocalDateTime updatedAt
) {

    public static UpdateMemberResponse fromEntity(Member member) {
        return builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImageUrl())
                .defaultProfileImg(member.getDefaultProfileImg())
                .introduce(member.getIntroduce())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
