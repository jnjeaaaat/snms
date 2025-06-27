package org.jnjeaaaat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.dto.CreatePostRequest;
import org.jnjeaaaat.domain.dto.CreatePostResponse;
import org.jnjeaaaat.domain.entity.Post;
import org.jnjeaaaat.domain.repository.PostRepository;
import org.jnjeaaaat.global.storage.StorageService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.jnjeaaaat.global.storage.FilePathType.POST;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final StorageService storageService;

    @Transactional
    public CreatePostResponse createPost(UserDetails userDetails,
                                         CreatePostRequest request,
                                         List<MultipartFile> postImages) {
        log.info("포스팅 유저 id: {}", userDetails.getUsername());

        Post savedPost = postRepository.save(
                Post.builder()
                        .memberId(Long.parseLong(userDetails.getUsername()))
                        .content(request.content())
                        .build()
        );

        List<String> postImageUrls =
                storageService.uploadImageList(POST, savedPost.getId(), postImages);

        savedPost.setPostImageUrls(postImageUrls);

        return CreatePostResponse.fromEntity(savedPost);
    }
}
