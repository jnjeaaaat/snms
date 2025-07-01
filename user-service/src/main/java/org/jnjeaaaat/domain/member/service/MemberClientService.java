package org.jnjeaaaat.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.jnjeaaaat.dto.member.ExistsMemberResponse;
import org.jnjeaaaat.dto.member.MemberInfoResponse;
import org.jnjeaaaat.exception.MemberException;
import org.springframework.stereotype.Service;

import static org.jnjeaaaat.global.exception.ErrorCode.NOT_FOUND_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberClientService {

    private final MemberRepository memberRepository;

    public ExistsMemberResponse checkMember(Long memberId) {
        return ExistsMemberResponse.builder()
                .exists(memberRepository.existsById(memberId))
                .build();
    }

    public MemberInfoResponse getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .map(member -> MemberInfoResponse.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .profileImageUrl(member.getProfileImageUrl())
                        .build())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
