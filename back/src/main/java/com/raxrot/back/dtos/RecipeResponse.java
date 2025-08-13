package com.raxrot.back.dtos;

import com.raxrot.back.enums.FoodDifficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {
    private Long id;
    private String title;
    private String description;
    private String ingredients;
    private String imageUrl;
    private FoodDifficulty difficulty;
    private UserResponseDTO author;
}
