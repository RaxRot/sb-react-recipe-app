package com.raxrot.back.security;

import com.raxrot.back.enums.UserRole;
import com.raxrot.back.models.User;
import com.raxrot.back.security.impl.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void build_shouldMapFieldsAndRole() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@mail.com");
        user.setUsername("alice");
        user.setPassword("pwd");
        user.setRole(UserRole.ROLE_ADMIN);

        // when
        UserDetailsImpl ud = UserDetailsImpl.build(user);

        // then
        assertThat(ud.getUsername()).isEqualTo("alice");
        assertThat(ud.getPassword()).isEqualTo("pwd");
        assertThat(ud.getEmail()).isEqualTo("alice@mail.com");

        var roles = ud.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertThat(roles).isEqualTo(Set.of("ROLE_ADMIN"));

        // default account flags
        assertThat(ud.isAccountNonExpired()).isTrue();
        assertThat(ud.isAccountNonLocked()).isTrue();
        assertThat(ud.isCredentialsNonExpired()).isTrue();
        assertThat(ud.isEnabled()).isTrue();
    }
}