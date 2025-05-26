package org.jnjeaaaat.snms.global.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .map(this::createUser)
                .orElseThrow();
    }

    private User createUser(Member member) {
        return new User(
                member.getEmail(),
                member.getPassword(),
                Collections.singleton(
                        new SimpleGrantedAuthority(member.getRole().name())
                )
        );
    }
}
