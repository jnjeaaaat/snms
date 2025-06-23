package org.jnjeaaaat.global.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.entity.Member;
import org.jnjeaaaat.domain.member.exception.MemberException;
import org.jnjeaaaat.domain.member.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static org.jnjeaaaat.global.exception.ErrorCode.NOT_FOUND_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return memberRepository.findByUid(uid)
                .map(this::createUser)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private User createUser(Member member) {
        return new User(
                String.valueOf(member.getId()),
                member.getPassword(),
                Collections.singleton(
                        new SimpleGrantedAuthority(member.getRole().name())
                )
        );
    }
}
