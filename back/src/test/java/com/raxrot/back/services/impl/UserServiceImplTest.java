package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.enums.UserRole;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.User;
import com.raxrot.back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserResponseDTO dto1;
    private UserResponseDTO dto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User(1L, "user1", "pass", "user1@mail.com", UserRole.ROLE_USER, null, null, null, null);
        user2 = new User(2L, "admin1", "pass", "admin@mail.com", UserRole.ROLE_ADMIN, null, null, null, null);

        dto1 = new UserResponseDTO(1L, "user1", "user1@mail.com", UserRole.ROLE_USER);
        dto2 = new UserResponseDTO(2L, "admin1", "admin@mail.com", UserRole.ROLE_ADMIN);
    }

    @Test
    void getAllUsers_shouldReturnOnlyRoleUser() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(modelMapper.map(user1, UserResponseDTO.class)).thenReturn(dto1);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).containsExactly(dto1);
        verify(userRepository).findAll();
        verify(modelMapper).map(user1, UserResponseDTO.class);
        verify(modelMapper, never()).map(user2, UserResponseDTO.class);
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(modelMapper.map(user1, UserResponseDTO.class)).thenReturn(dto1);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isEqualTo(dto1);
        verify(userRepository).findById(1L);
        verify(modelMapper).map(user1, UserResponseDTO.class);
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ApiException.class)
                .hasMessage("User not found");
    }

    @Test
    void deleteUser_shouldDelete_whenRoleUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.deleteUser(1L);

        verify(userRepository).delete(user1);
    }

    @Test
    void deleteUser_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(ApiException.class)
                .hasMessage("User not found");
    }

    @Test
    void deleteUser_shouldThrow_whenRoleAdmin() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        assertThatThrownBy(() -> userService.deleteUser(2L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Impossible to delete admin")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository, never()).delete(any());
    }
}