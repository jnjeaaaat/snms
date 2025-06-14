package org.jnjeaaaat.snms.domain.member.repository;

import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.entity.MemberProvider;
import org.jnjeaaaat.snms.domain.member.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {

    Optional<MemberProvider> findByProviderNameAndProviderUserId(LoginType providerName, String providerUserId);
    Optional<MemberProvider> findByMember(Member member);

}
