package com.raxrot.back.security.dto;

import com.raxrot.back.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String username;
    private UserRole role;
    private String jwtToken;
}
