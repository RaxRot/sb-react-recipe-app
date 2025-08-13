package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.enums.UserRole;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.User;
import com.raxrot.back.repository.UserRepository;
import com.raxrot.back.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.ROLE_USER)
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new ApiException("User not found", HttpStatus.NOT_FOUND));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (user.getRole().equals(UserRole.ROLE_USER)) {
            userRepository.delete(user);
        }else{
            throw new ApiException("Impossible to delete admin", HttpStatus.FORBIDDEN);
        }
    }
}
