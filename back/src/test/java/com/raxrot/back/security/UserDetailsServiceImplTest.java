package com.raxrot.back.security;

import com.raxrot.back.enums.UserRole;
import com.raxrot.back.models.User;
import com.raxrot.back.repository.UserRepository;
import com.raxrot.back.security.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(7L);
        user.setUsername("bob");
        user.setEmail("bob@mail.com");
        user.setPassword("pwd");
        user.setRole(UserRole.ROLE_USER);
    }

    @Test
    void loadUserByUsername_shouldReturnDetails_whenUserExists() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("bob");

        assertThat(details.getUsername()).isEqualTo("bob");
        assertThat(details.getPassword()).isEqualTo("pwd");
        assertThat(details.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_USER");

        verify(userRepository).findByUsername("bob");
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserMissing() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown"));

        verify(userRepository).findByUsername("unknown");
    }
}