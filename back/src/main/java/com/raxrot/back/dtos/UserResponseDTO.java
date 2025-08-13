package com.raxrot.back.dtos;

import com.raxrot.back.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO
{
    private Long id;
    private String username;
    private String email;
    private UserRole role;
}
