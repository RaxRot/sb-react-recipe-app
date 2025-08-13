package com.raxrot.back.services;

import com.raxrot.back.dtos.RecipeRequest;
import com.raxrot.back.dtos.RecipeResponse;
import com.raxrot.back.enums.FoodDifficulty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
    RecipeResponse createRecipe(RecipeRequest recipeRequest, MultipartFile file);
    List<RecipeResponse> getAllRecipes();
    RecipeResponse getRecipe(Long id);
    RecipeResponse updateRecipe(Long id, RecipeRequest recipeRequest);
    void deleteRecipe(Long id);
    List<RecipeResponse> getAllRecipesByDifficulty(FoodDifficulty difficulty);
    List<RecipeResponse> getAllRecipesByAuthorId(Long authorId);
}
