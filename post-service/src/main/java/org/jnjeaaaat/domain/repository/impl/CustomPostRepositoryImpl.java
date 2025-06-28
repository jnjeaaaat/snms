package org.jnjeaaaat.domain.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.dto.post.PostsResponse;
import org.jnjeaaaat.domain.repository.CustomPostRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jnjeaaaat.domain.entity.QPost.post;
import static org.jnjeaaaat.domain.entity.QPostImage.postImage;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<PostsResponse> findPostsByMemberId(Long loginMemberId, Long targetMemberId, Pageable pageable) {
        List<PostsResponse> content = jpaQueryFactory
                .select(Projections.constructor(PostsResponse.class,
                        post.id,
                        post.memberId,
                        post.content,
                        JPAExpressions
                                .select(postImage.imageUrl)
                                .from(postImage)
                                .where(
                                        postImage.post.eq(post),
                                        postImage.isThumbnail.isTrue()
                                )
                                .limit(1),
                        post.createdAt,
                        post.updatedAt
                ))
                .from(post)
                .where(
                        post.memberId.eq(targetMemberId)
                                .and(
                                        loginMemberId.equals(targetMemberId)
                                                ? null // 본인이면 숨김 제외 조건 X
                                                : post.isPublic.isTrue() // 타인이라면 공개 게시글만
                                )
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)  // hasNext 체크 위해 +1
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
