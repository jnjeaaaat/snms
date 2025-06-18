package org.jnjeaaaat.snms.domain.member.dto.response;

import lombok.Builder;
import org.jnjeaaaat.snms.domain.member.entity.Member;

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
        return UpdateMemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .defaultProfileImg(member.getDefaultProfileImg())
                .introduce(member.getIntroduce())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
