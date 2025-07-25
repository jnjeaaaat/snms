package org.jnjeaaaat.global.client.post;

import org.jnjeaaaat.dto.CustomPageRequest;
import org.jnjeaaaat.dto.post.PostsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "member-service",
        url = "${feign.client.post-service.url}",
        path = "/client/posts"
)
public interface PostClient {

    @GetMapping("/members/{targetMemberId}")
    Slice<PostsResponse> getPostsByMemberId(
            @PathVariable Long targetMemberId,
            Long loginMemberId,
            CustomPageRequest pageRequest);

}
