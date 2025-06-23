package org.jnjeaaaat.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.domain.member.dto.request.UpdateMemberRequest;
import org.jnjeaaaat.domain.member.dto.response.UpdateMemberResponse;
import org.jnjeaaaat.domain.member.service.MemberService;
import org.jnjeaaaat.global.validator.annotation.ValidFile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.jnjeaaaat.global.util.LogUtil.logInfo;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> updateMemberInfo(
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long memberId,
            @RequestPart(value = "request") @Valid UpdateMemberRequest updateMemberRequest,
            @RequestPart(value = "file", required = false) @ValidFile MultipartFile profileImg) {

        logInfo(request, "사용자 정보 업데이트");

        return ResponseEntity.ok(
                memberService.updateMemberInfo(userDetails, memberId, updateMemberRequest, profileImg)
        );
    }


}
