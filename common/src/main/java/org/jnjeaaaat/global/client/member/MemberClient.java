package org.jnjeaaaat.global.client.member;

import org.jnjeaaaat.dto.member.ExistsMemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "member-service",
        url = "${feign.client.member-service.url}",
        path = "/client/members"
)
public interface MemberClient {

    @GetMapping("/exist/{memberId}")
    ExistsMemberResponse checkMember(@PathVariable Long memberId);
}
