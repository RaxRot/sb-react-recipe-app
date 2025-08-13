package com.raxrot.back.services;

import com.raxrot.back.dtos.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO>getAllUsers();
    UserResponseDTO getUserById(Long id);
    void deleteUser(Long id);
}
