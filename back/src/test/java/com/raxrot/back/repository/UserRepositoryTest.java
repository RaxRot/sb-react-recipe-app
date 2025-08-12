package com.raxrot.back.repository;

import com.raxrot.back.enums.UserRole;
import com.raxrot.back.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired UserRepository userRepository;

    private User u1;
    private User u2;

    @BeforeEach
    void setUp() {
        u1 = new User(null, "alice", "pwd", "alice@mail.com", UserRole.ROLE_USER, null, null, null, null);
        u2 = new User(null, "bob",   "pwd", "bob@mail.com",   UserRole.ROLE_ADMIN, null, null, null, null);
        em.persist(u1);
        em.persist(u2);
        em.flush();
        em.clear();
    }

    @Test
    void findByUsername_shouldReturnUser() {
        Optional<User> found = userRepository.findByUsername("alice");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    void findByUsername_shouldBeEmpty_whenNoUser() {
        assertThat(userRepository.findByUsername("nope")).isNotPresent();
    }

    @Test
    void findByEmail_shouldReturnUser() {
        Optional<User> found = userRepository.findByEmail("bob@mail.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("bob");
    }

    @Test
    void findByEmail_shouldBeEmpty_whenNoUser() {
        assertThat(userRepository.findByEmail("none@mail.com")).isNotPresent();
    }

    @Test
    void findByRole_shouldReturnUser() {
        Optional<User> found = userRepository.findByRole(UserRole.ROLE_ADMIN);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("bob");
    }

    @Test
    void findByRole_shouldBeEmpty_whenNoUserWithThatRole() {
        em.clear();
        userRepository.deleteAll();
        assertThat(userRepository.findByRole(UserRole.ROLE_ADMIN)).isNotPresent();
    }
}