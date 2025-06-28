package org.jnjeaaaat.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.service.MemberClientService;
import org.jnjeaaaat.dto.member.ExistsMemberResponse;
import org.jnjeaaaat.dto.member.MemberInfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/members")
public class MemberClientController {

    private final MemberClientService memberClientService;

    @GetMapping("/exist/{memberId}")
    public ExistsMemberResponse checkMember(@PathVariable Long memberId) {
        return memberClientService.checkMember(memberId);
    }

    @GetMapping("/{memberId}")
    public MemberInfoResponse getMemberInfo(@PathVariable Long memberId) {
        return memberClientService.getMemberInfo(memberId);
    }
}
