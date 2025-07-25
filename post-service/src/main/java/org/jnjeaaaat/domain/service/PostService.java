package org.jnjeaaaat.domain.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.dto.request.CreatePostRequest;
import org.jnjeaaaat.domain.dto.response.CreatePostResponse;
import org.jnjeaaaat.domain.dto.response.PostInfoResponse;
import org.jnjeaaaat.domain.entity.Post;
import org.jnjeaaaat.domain.repository.PostRepository;
import org.jnjeaaaat.dto.member.MemberInfoResponse;
import org.jnjeaaaat.exception.MemberException;
import org.jnjeaaaat.exception.PostException;
import org.jnjeaaaat.global.client.excpetion.ExternalApiException;
import org.jnjeaaaat.global.client.member.MemberClient;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.jnjeaaaat.global.event.dto.PostCreatedEventPayload;
import org.jnjeaaaat.global.event.type.EventType;
import org.jnjeaaaat.global.kafka.producer.EventProducer;
import org.jnjeaaaat.global.storage.StorageService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.jnjeaaaat.global.constant.EventCons.POST_EVENT_TOPIC;
import static org.jnjeaaaat.global.constant.EventCons.POST_SERVICE_MODULE;
import static org.jnjeaaaat.global.exception.ErrorCode.*;
import static org.jnjeaaaat.global.storage.FilePathType.POST;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberClient memberClient;
    private final PostRepository postRepository;
    private final StorageService storageService;
    private final EventProducer eventProducer;

    /**
     * 포스팅 생성
     *
     * @param userDetails @AuthenticationPrincipal 엑세스 토큰 값으로 추출한 User 객체
     * @param request     CreatePostRequest 포스팅하고자 하는 값 content
     * @param postImages  업로드 하고자 하는 Post Image List
     * @return CreatePostResponse
     * <br/> Long id,
     * <br/> Long memberId
     */
    @Transactional
    public CreatePostResponse createPost(UserDetails userDetails,
                                         CreatePostRequest request,
                                         List<MultipartFile> postImages) {
        log.info("포스팅 유저 id: {}", userDetails.getUsername());

        validateMember(Long.parseLong(userDetails.getUsername()));

        Post savedPost = postRepository.save(
                Post.builder()
                        .memberId(Long.parseLong(userDetails.getUsername()))
                        .content(request.content())
                        .build()
        );

        List<String> postImageUrls =
                storageService.uploadImageList(POST, savedPost.getId(), postImages);

        savedPost.setPostImageUrls(postImageUrls);

        // Notification event 발행
        NotificationEvent<PostCreatedEventPayload> event = NotificationEvent.of(
                PostCreatedEventPayload.of(
                        savedPost.getId(),
                        savedPost.getMemberId(),
                        getMemberInfo(savedPost.getMemberId()).uid()
                ),
                EventType.POST_CREATED,
                POST_SERVICE_MODULE
        );

        eventProducer.send(
                POST_EVENT_TOPIC,
                event
        );

        return CreatePostResponse.fromEntity(savedPost);
    }

    /**
     * 포스팅 정보 조회
     *
     * @param userDetails @AuthenticationPrincipal 엑세스 토큰 값으로 추출한 User 객체
     * @param postId      포스팅 정보 조회를 위한 PathVariable 값
     * @return PostInfoResponse
     * <br/> Long id,
     * <br/> MemberInfoResponse member,
     * <br/> String content,
     * <br/> List<PostImagesResponse> postImageUrls
     */
    public PostInfoResponse getPostInfo(UserDetails userDetails, Long postId) {

        log.info("포스팅 정보 조회 요청, postId: {}", postId);

        Long memberId = userDetails != null ?
                Long.parseLong(userDetails.getUsername()) : null;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST));

        // 작성자가 아닌데 숨겨진 게시글 일때
        if (!post.getMemberId().equals(memberId) && !post.getIsPublic()) {
            throw new PostException(POST_NOT_PUBLIC);
        }

        return PostInfoResponse.fromEntity(
                post,
                getMemberInfo(post.getMemberId())
        );
    }

    // 멤버 유효성 검사
    private void validateMember(Long memberId) {
        try {
            if (!memberClient.checkMember(memberId).exists()) {
                throw new MemberException(NOT_FOUND_MEMBER);
            }
        } catch (FeignException e) {
            throw new ExternalApiException(EXTERNAL_API_ERROR, e.getMessage());
        }
    }

    private MemberInfoResponse getMemberInfo(Long memberId) {
        try {
            return memberClient.getMemberInfo(memberId);
        } catch (FeignException e) {
            throw new ExternalApiException(EXTERNAL_API_ERROR, e.getMessage());
        }
    }
}
