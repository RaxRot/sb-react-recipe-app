package com.raxrot.back.dtos;

import com.raxrot.back.enums.FoodDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @NotBlank(message = "Ingredients are required")
    @Size(max = 2000, message = "Ingredients must be at most 2000 characters")
    private String ingredients;

    @Size(max = 255, message = "Image URL must be at most 255 characters")
    private String image;

    @NotNull(message = "Difficulty is required")
    private FoodDifficulty difficulty;
}