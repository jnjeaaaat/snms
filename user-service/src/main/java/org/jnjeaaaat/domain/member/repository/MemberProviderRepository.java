package org.jnjeaaaat.domain.member.repository;

import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.entity.MemberProvider;
import org.jnjeaaaat.domain.member.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {

    Optional<MemberProvider> findByProviderNameAndProviderUserId(LoginType providerName, String providerUserId);
    Optional<MemberProvider> findByMember(Member member);

}
