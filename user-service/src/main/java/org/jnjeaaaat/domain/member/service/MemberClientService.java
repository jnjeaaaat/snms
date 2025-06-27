package org.jnjeaaaat.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.jnjeaaaat.dto.member.ExistsMemberResponse;
import org.springframework.stereotype.Service;

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
}
