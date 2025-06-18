package org.jnjeaaaat.snms.global.util;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserTestFixture {

    public static UserDetails createTestUser() {
        return User.builder()
                .username("1")
                .password("qwerQW!@")
                .authorities("ROLE_USER")
                .build();
    }

    public static UserDetails createTestAdmin() {
        return User.builder()
                .username("1")
                .password("qwerQW!@")
                .authorities("ROLE_ADMIN")
                .build();
    }

    public static UserDetails createCustomUser(String username, String... roles) {
        return User.builder()
                .username(username)
                .password("qwerQW!@")
                .authorities(roles)
                .build();
    }
}
