package org.jnjeaaaat.domain.service;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.dto.post.PostsResponse;
import org.jnjeaaaat.domain.repository.CustomPostRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostClientService {

    private final CustomPostRepository customPostRepository;

    public Slice<PostsResponse> getPostsByMemberId(Long loginMemberId, Long targetMemberId, Pageable pageable) {
        return customPostRepository
                .findPostsByMemberId(loginMemberId, targetMemberId, pageable);
    }

}
