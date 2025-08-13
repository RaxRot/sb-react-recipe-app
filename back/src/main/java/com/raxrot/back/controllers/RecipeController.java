package com.raxrot.back.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxrot.back.dtos.RecipeRequest;
import com.raxrot.back.dtos.RecipeResponse;
import com.raxrot.back.enums.FoodDifficulty;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final RecipeService recipeService;
    private final ObjectMapper objectMapper;

    public RecipeController(RecipeService recipeService, ObjectMapper objectMapper) {
        this.recipeService = recipeService;
        this.objectMapper = objectMapper;
    }

    //--- FOR ALL ---

    @GetMapping("/public/recipes")
    public ResponseEntity<List<RecipeResponse>> getAll() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/public/recipes/{id}")
    public ResponseEntity<RecipeResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipe(id));
    }

    @GetMapping("/public/recipes/difficulty/{difficulty}")
    public ResponseEntity<List<RecipeResponse>> byDifficulty(@PathVariable FoodDifficulty difficulty) {
        return ResponseEntity.ok(recipeService.getAllRecipesByDifficulty(difficulty));
    }

    @GetMapping("/public/recipes/author/{authorId}")
    public ResponseEntity<List<RecipeResponse>> byAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(recipeService.getAllRecipesByAuthorId(authorId));
    }

    //--- FOR AUTH AND ADMIN --

    @PostMapping(value = "/recipes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> create(
            @RequestPart("data") String requestString,
            @RequestPart("file") MultipartFile file) {

        try {
            RecipeRequest recipeRequest = objectMapper.readValue(requestString, RecipeRequest.class);
            RecipeResponse response = recipeService.createRecipe(recipeRequest, file);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            throw new ApiException("Invalid JSON format for recipe data", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/recipes/{id}")
    public ResponseEntity<RecipeResponse> update(@PathVariable Long id,@RequestBody @Valid RecipeRequest request) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, request));
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
