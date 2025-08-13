package com.raxrot.back.services;

import com.raxrot.back.dtos.RecipeRequest;
import com.raxrot.back.dtos.RecipeResponse;
import com.raxrot.back.enums.FoodDifficulty;
import com.raxrot.back.enums.UserRole;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Recipe;
import com.raxrot.back.models.User;
import com.raxrot.back.repository.RecipeRepository;
import com.raxrot.back.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final ModelMapper modelMapper;
    private final AuthUtils authUtils;
    private final FileUploadService fileUploadService;
    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             ModelMapper modelMapper,
                             AuthUtils authUtils,
                             FileUploadService fileUploadService) {
        this.recipeRepository = recipeRepository;
        this.modelMapper = modelMapper;
        this.authUtils = authUtils;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public RecipeResponse createRecipe(RecipeRequest recipeRequest, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Image file is required", HttpStatus.BAD_REQUEST);
        }

        User user = authUtils.getCurrentUser();
        String imageUrl=fileUploadService.uploadFile(file);
        Recipe recipe = modelMapper.map(recipeRequest, Recipe.class);
        recipe.setAuthor(user);
        recipe.setImageUrl(imageUrl);
        return modelMapper.map(recipeRepository.save(recipe), RecipeResponse.class);
    }

    @Override
    public List<RecipeResponse> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<RecipeResponse>recipeResponses=recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponse.class)).collect(Collectors.toList());
        return recipeResponses;
    }

    @Override
    public List<RecipeResponse> getAllRecipesByDifficulty(FoodDifficulty difficulty) {
        List<Recipe>recipes=recipeRepository.findByDifficulty(difficulty);
        List<RecipeResponse>recipeResponses=recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponse.class)).collect(Collectors.toList());
        return recipeResponses;
    }

    @Override
    public List<RecipeResponse> getAllRecipesByAuthorId(Long authorId) {
        List<Recipe> recipes = recipeRepository.findByAuthor_Id(authorId);
        return recipes.stream()
                .map(r -> modelMapper.map(r, RecipeResponse.class))
                .toList();
    }

    @Override
    public RecipeResponse getRecipe(Long id) {
        Recipe recipe=recipeRepository.findById(id)
                .orElseThrow(()->new ApiException("Recipe not found",HttpStatus.NOT_FOUND));
        return modelMapper.map(recipe, RecipeResponse.class);
    }

    @Override
    public RecipeResponse updateRecipe(Long id, RecipeRequest recipeRequest) {
        User currentUser = authUtils.getCurrentUser();

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Recipe not found", HttpStatus.NOT_FOUND));

        // check rights
        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN) &&
                !recipe.getAuthor().getId().equals(currentUser.getId())) {
            throw new ApiException("You don't have permission to update this recipe", HttpStatus.FORBIDDEN);
        }

        recipe.setTitle(recipeRequest.getTitle());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setIngredients(recipeRequest.getIngredients());
        recipe.setDifficulty(recipeRequest.getDifficulty());

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return modelMapper.map(updatedRecipe, RecipeResponse.class);
    }

    @Override
    public void deleteRecipe(Long id) {
        User currentUser = authUtils.getCurrentUser();

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Recipe not found", HttpStatus.NOT_FOUND));

        // check rights
        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN) &&
                !recipe.getAuthor().getId().equals(currentUser.getId())) {
            throw new ApiException("You don't have permission to delete this recipe", HttpStatus.FORBIDDEN);
        }
        String imageToDelete=recipe.getImageUrl();
        fileUploadService.deleteFile(imageToDelete);
        recipeRepository.delete(recipe);
    }
}
