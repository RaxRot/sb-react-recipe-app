package com.raxrot.back.repository;

import com.raxrot.back.enums.UserRole;
import com.raxrot.back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User>findByRole(UserRole role);
}
