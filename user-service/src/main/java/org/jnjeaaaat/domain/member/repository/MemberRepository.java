package org.jnjeaaaat.domain.member.repository;

import org.jnjeaaaat.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUid(String uid);
    Optional<Member> findByPhoneNum(String phoneNum);

    boolean existsByUid(String uid);

    boolean existsByPhoneNum(String phoneNum);
}
