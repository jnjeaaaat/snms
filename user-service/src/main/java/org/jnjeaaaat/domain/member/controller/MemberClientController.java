package org.jnjeaaaat.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.domain.member.service.MemberClientService;
import org.jnjeaaaat.domain.member.service.MemberService;
import org.jnjeaaaat.dto.member.ExistsMemberResponse;
import org.jnjeaaaat.dto.member.FollowerInfoResponse;
import org.jnjeaaaat.dto.member.MemberInfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/members")
public class MemberClientController {

    private final MemberClientService memberClientService;
    private final MemberService memberService;

    @GetMapping("/exist/{memberId}")
    public ExistsMemberResponse checkMember(@PathVariable Long memberId) {
        return memberClientService.checkMember(memberId);
    }

    @GetMapping("/{memberId}")
    public MemberInfoResponse getMemberInfo(@PathVariable Long memberId) {
        return memberClientService.getMemberInfo(memberId);
    }

    @GetMapping("/{targetMemberId}/followers")
    public List<FollowerInfoResponse> getFollowers(@PathVariable Long targetMemberId) {
        return memberService.getFollowers(null, targetMemberId);
    }
}
