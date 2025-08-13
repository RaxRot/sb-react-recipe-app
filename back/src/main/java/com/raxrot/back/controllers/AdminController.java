package com.raxrot.back.controllers;

import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>>getUsers() {
        List<UserResponseDTO>userResponseDTOS=userService.getAllUsers();
        return ResponseEntity.ok().body(userResponseDTOS);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long userId) {
        UserResponseDTO userResponseDTO=userService.getUserById(userId);
        return ResponseEntity.ok().body(userResponseDTO);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void>deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
