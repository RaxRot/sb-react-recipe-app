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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private User user;
    private User admin;
    private Recipe recipe;
    private RecipeRequest recipeRequest;
    private RecipeResponse recipeResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "user", "pwd", "user@mail.com", UserRole.ROLE_USER, null, null, null, null);
        admin = new User(2L, "admin", "pwd", "admin@mail.com", UserRole.ROLE_ADMIN, null, null, null, null);

        recipe = new Recipe(10L, "title", "desc", "ing", "imgUrl", FoodDifficulty.EASY, null, null, user, List.of());
        recipeRequest = new RecipeRequest("newTitle", "newDesc", "newIng", "newImg", FoodDifficulty.HARD);
        recipeResponse = new RecipeResponse(10L, "title", "desc", "ing", "imgUrl", FoodDifficulty.EASY, null);
    }

    @Test
    void createRecipe_shouldUploadFileAndSaveRecipe() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(authUtils.getCurrentUser()).thenReturn(user);
        when(fileUploadService.uploadFile(file)).thenReturn("url");
        when(modelMapper.map(recipeRequest, Recipe.class)).thenReturn(recipe);
        when(recipeRepository.save(any())).thenReturn(recipe);
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        RecipeResponse result = recipeService.createRecipe(recipeRequest, file);

        assertThat(result).isEqualTo(recipeResponse);
        verify(fileUploadService).uploadFile(file);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void createRecipe_shouldThrowIfFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> recipeService.createRecipe(recipeRequest, file))
                .isInstanceOf(ApiException.class)
                .hasMessage("Image file is required")
                .extracting("status").isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllRecipes_shouldReturnSortedMappedList() {
        List<Recipe> recipes = List.of(recipe);
        when(recipeRepository.findAll(any(Sort.class))).thenReturn(recipes);
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        List<RecipeResponse> result = recipeService.getAllRecipes();

        assertThat(result).containsExactly(recipeResponse);
    }

    @Test
    void getAllRecipesByDifficulty_shouldMapResults() {
        when(recipeRepository.findByDifficulty(FoodDifficulty.EASY)).thenReturn(List.of(recipe));
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        List<RecipeResponse> result = recipeService.getAllRecipesByDifficulty(FoodDifficulty.EASY);

        assertThat(result).containsExactly(recipeResponse);
    }

    @Test
    void getAllRecipesByAuthorId_shouldMapResults() {
        when(recipeRepository.findByAuthor_Id(1L)).thenReturn(List.of(recipe));
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        List<RecipeResponse> result = recipeService.getAllRecipesByAuthorId(1L);

        assertThat(result).containsExactly(recipeResponse);
    }

    @Test
    void getRecipe_shouldReturnMappedRecipe() {
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        RecipeResponse result = recipeService.getRecipe(10L);

        assertThat(result).isEqualTo(recipeResponse);
    }

    @Test
    void getRecipe_shouldThrowIfNotFound() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipe(99L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Recipe not found")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRecipe_shouldAllowAdminToUpdate() {
        when(authUtils.getCurrentUser()).thenReturn(admin);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        RecipeResponse result = recipeService.updateRecipe(10L, recipeRequest);

        assertThat(result).isEqualTo(recipeResponse);
        assertThat(recipe.getTitle()).isEqualTo("newTitle");
    }

    @Test
    void updateRecipe_shouldAllowAuthorToUpdateOwnRecipe() {
        when(authUtils.getCurrentUser()).thenReturn(user);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(modelMapper.map(recipe, RecipeResponse.class)).thenReturn(recipeResponse);

        RecipeResponse result = recipeService.updateRecipe(10L, recipeRequest);

        assertThat(result).isEqualTo(recipeResponse);
    }

    @Test
    void updateRecipe_shouldThrowIfNotAuthorOrAdmin() {
        User stranger = new User(3L, "stranger", "pwd", "s@mail.com", UserRole.ROLE_USER, null, null, null, null);
        when(authUtils.getCurrentUser()).thenReturn(stranger);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.updateRecipe(10L, recipeRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("permission")
                .extracting("status").isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteRecipe_shouldAllowAdminToDelete() {
        when(authUtils.getCurrentUser()).thenReturn(admin);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(10L);

        verify(fileUploadService).deleteFile("imgUrl");
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipe_shouldAllowAuthorToDeleteOwnRecipe() {
        when(authUtils.getCurrentUser()).thenReturn(user);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(10L);

        verify(fileUploadService).deleteFile("imgUrl");
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipe_shouldThrowIfNotAuthorOrAdmin() {
        User stranger = new User(3L, "stranger", "pwd", "s@mail.com", UserRole.ROLE_USER, null, null, null, null);
        when(authUtils.getCurrentUser()).thenReturn(stranger);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.deleteRecipe(10L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("permission")
                .extracting("status").isEqualTo(HttpStatus.FORBIDDEN);
    }
}