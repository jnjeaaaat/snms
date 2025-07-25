package org.jnjeaaaat.global.client.member;

import org.jnjeaaaat.dto.member.ExistsMemberResponse;
import org.jnjeaaaat.dto.member.FollowerInfoResponse;
import org.jnjeaaaat.dto.member.MemberInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "member-service",
        url = "${feign.client.member-service.url}",
        path = "/client/members"
)
public interface MemberClient {

    @GetMapping("/exist/{memberId}")
    ExistsMemberResponse checkMember(@PathVariable Long memberId);

    @GetMapping("/{memberId}")
    MemberInfoResponse getMemberInfo(@PathVariable Long memberId);

    @GetMapping("/{targetMemberId}/followers")
    List<FollowerInfoResponse> getFollowers(@PathVariable Long targetMemberId);
}
