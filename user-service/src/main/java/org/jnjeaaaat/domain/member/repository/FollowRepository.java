package org.jnjeaaaat.domain.member.repository;

import org.jnjeaaaat.domain.member.entity.Follow;
import org.jnjeaaaat.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    List<Follow> findAllByFollowing(Member following);

    boolean existsByFollowerAndFollowing(Member follower, Member following);

    void deleteByFollowerAndFollowing(Member follower, Member following);
}
