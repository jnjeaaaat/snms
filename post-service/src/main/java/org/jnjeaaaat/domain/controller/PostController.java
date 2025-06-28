package org.jnjeaaaat.domain.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.dto.request.CreatePostRequest;
import org.jnjeaaaat.domain.dto.response.CreatePostResponse;
import org.jnjeaaaat.domain.dto.response.PostInfoResponse;
import org.jnjeaaaat.domain.service.PostClientService;
import org.jnjeaaaat.domain.service.PostService;
import org.jnjeaaaat.dto.CustomPageRequest;
import org.jnjeaaaat.dto.TestDto;
import org.jnjeaaaat.dto.post.PostsResponse;
import org.jnjeaaaat.global.validator.annotation.ValidFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.jnjeaaaat.global.util.LogUtil.logInfo;

@RestController
@RequestMapping("/api/posts")
@Validated
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostClientService postClientService; // todo: 테스트용 추후 삭제

    @GetMapping("")
    public ResponseEntity<TestDto> postTest(HttpServletRequest request) {
        logInfo(request, "Post Test Success");
        return ResponseEntity.ok(
                new TestDto("Post Dto Share Success")
        );
    }

    @PostMapping("")
    public ResponseEntity<CreatePostResponse> createPost(
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(value = "request") @Valid CreatePostRequest createPostRequest,
            @RequestPart(value = "files") @ValidFile List<MultipartFile> files) {

        logInfo(request, "포스팅 요청");

        return ResponseEntity.ok(
                postService.createPost(userDetails,
                        createPostRequest,
                        files
                )
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoResponse> getPostInfo(
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId) {

        logInfo(request, "포스팅 정보 요청");

        return ResponseEntity.ok(
                postService.getPostInfo(
                        userDetails,
                        postId
                )
        );
    }

    // todo: postman 테스트용 후에 삭제
    @GetMapping("/members/{targetMemberId}")
    public Slice<PostsResponse> getPostsByMemberId(@PathVariable Long targetMemberId,
                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody CustomPageRequest pageRequest) {
        return postClientService.getPostsByMemberId(
                Long.parseLong(userDetails.getUsername()),
                targetMemberId,
                PageRequest.of(
                        pageRequest.page(),
                        CustomPageRequest.PAGE_SIZE
                )
        );
    }

}
