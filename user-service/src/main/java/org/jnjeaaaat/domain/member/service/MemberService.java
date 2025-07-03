package org.jnjeaaaat.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.dto.request.UpdateMemberRequest;
import org.jnjeaaaat.domain.member.dto.response.UpdateMemberResponse;
import org.jnjeaaaat.domain.member.entity.Follow;
import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.repository.FollowRepository;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.jnjeaaaat.domain.member.type.CountType;
import org.jnjeaaaat.exception.MemberException;
import org.jnjeaaaat.global.storage.StorageService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

import static org.jnjeaaaat.global.exception.ErrorCode.*;
import static org.jnjeaaaat.global.storage.FilePathType.MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final StorageService storageService;
    private final RedisCountService redisCountService;

    /**
     * Update Member Info
     *
     * @param userDetails @AuthenticationPrincipal 엑세스 토큰 값으로 추출한 User 객체
     * @param memberId    @PathVariable 값으로 userDetails.getUsername() 비교를 위한 Member PK
     * @param request     Member 정보 수정하고자 하는 값 nickname, introduce
     * @param profileImg  업로드 하고자 하는 Member ProfileImg
     * @return new UpdateMemberResponse
     * <br/> Long id,
     * <br/> String nickname,
     * <br/> String profileImgUrl,
     * <br/> Boolean defaultProfileImg,
     * <br/> String introduce,
     * <br/> LocalDateTime updatedAt
     * <br/> Member 의 새로운 Information
     */
    @Transactional
    public UpdateMemberResponse updateMemberInfo(UserDetails userDetails,
                                                 Long memberId,
                                                 UpdateMemberRequest request,
                                                 MultipartFile profileImg) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        validateMember(member, userDetails);

        Optional.ofNullable(request.nickname())
                .filter(StringUtils::hasText)
                .ifPresent(member::updateNickname);

        Optional.ofNullable(request.introduce())
                .ifPresent(member::updateIntroduce);

        Optional.ofNullable(profileImg)
                .filter(file -> !file.isEmpty())
                .ifPresent(file -> updateMemberProfileImage(member, file));

        return UpdateMemberResponse.fromEntity(member);
    }

    private void validateMember(Member member, UserDetails userDetails) {
        if (!ObjectUtils.isEmpty(member.getDeletedAt())) {
            throw new MemberException(ALREADY_DELETED_MEMBER);
        }

        if (!Objects.equals(member.getId(), Long.valueOf(userDetails.getUsername()))) {
            throw new MemberException(AUTHENTICATION_MEMBER_MISMATCH);
        }
    }

    private void updateMemberProfileImage(Member member, MultipartFile file) {
        String originProfileUrl = member.getProfileImageUrl();
        String imageUrl = storageService.uploadImage(MEMBER, member.getId(), file);
        member.updateProfileImage(imageUrl);

        if (member.getDefaultProfileImg()) {
            member.switchDefaultProfileImg();
        } else {
            storageService.deleteFile(originProfileUrl);
        }
    }

    /**
     * Follow Member
     *
     * @param userDetails    @AuthenticationPrincipal 엑세스 토큰 값으로 추출한 User 객체
     * @param followMemberId @PathVariable 값으로 userDetails.getUsername() 비교를 위한 Member PK
     */
    @Transactional
    public void followMember(UserDetails userDetails, Long followMemberId) {
        Member follower = memberRepository.findById(Long.valueOf(userDetails.getUsername()))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        Member following = memberRepository.findById(followMemberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        validateFollowInfo(follower, following);

        redisCountService.checkCount(Long.valueOf(userDetails.getUsername()), followMemberId, CountType.FOLLOW);

        followRepository.save(
                Follow.builder()
                        .follower(follower)
                        .following(following)
                        .build()
        );
    }

    private void validateFollowInfo(Member follower, Member following) {
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new MemberException(ALREADY_FOLLOWING_MEMBER);
        }

        if (follower.getId().equals(following.getId())) {
            throw new MemberException(CANNOT_FOLLOW_SELF);
        }
    }

    /**
     * Unfollow Member
     *
     * @param userDetails    @AuthenticationPrincipal 엑세스 토큰 값으로 추출한 User 객체
     * @param followMemberId @PathVariable 값으로 userDetails.getUsername() 비교를 위한 Member PK
     */
    @Transactional
    public void unfollowMember(UserDetails userDetails, Long followMemberId) {
        Member follower = memberRepository.findById(Long.valueOf(userDetails.getUsername()))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        Member following = memberRepository.findById(followMemberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        Follow followInfo = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new MemberException(NOT_FOUND_FOLLOW));

        redisCountService.checkCount(Long.valueOf(userDetails.getUsername()), followMemberId, CountType.FOLLOW);

        followRepository.deleteByFollowerAndFollowing(followInfo.getFollower(), followInfo.getFollowing());
    }
}
