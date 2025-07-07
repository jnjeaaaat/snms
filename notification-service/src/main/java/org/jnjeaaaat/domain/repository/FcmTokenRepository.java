package org.jnjeaaaat.domain.repository;

import org.jnjeaaaat.domain.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByMemberIdAndUserAgent(Long memberId, String userAgent);
}
