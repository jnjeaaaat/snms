package org.jnjeaaaat.domain.repository;

import org.jnjeaaaat.dto.post.PostsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomPostRepository {

    Slice<PostsResponse> findPostsByMemberId(Long loginMemberId, Long targetMemberId, Pageable pageable);
}
