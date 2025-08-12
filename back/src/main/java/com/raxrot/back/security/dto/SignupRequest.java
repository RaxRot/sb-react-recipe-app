package com.raxrot.back.security.dto;

import com.raxrot.back.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Setter
    @Getter
    private UserRole role;

    @NotBlank
    @Size(min = 5, max = 40)
    private String password;
}
