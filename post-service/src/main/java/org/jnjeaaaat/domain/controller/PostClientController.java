package org.jnjeaaaat.domain.controller;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.dto.CustomPageRequest;
import org.jnjeaaaat.dto.post.PostsResponse;
import org.jnjeaaaat.domain.service.PostClientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/posts")
public class PostClientController {

    private final PostClientService postClientService;

    @GetMapping("/members/{targetMemberId}")
    public Slice<PostsResponse> getPostsByMemberId(@PathVariable Long targetMemberId,
                                                   Long loginMemberId,
                                                   CustomPageRequest pageRequest) {
        return postClientService.getPostsByMemberId(
                loginMemberId,
                targetMemberId,
                PageRequest.of(
                        pageRequest.page(),
                        CustomPageRequest.PAGE_SIZE
                )
        );
    }
}
