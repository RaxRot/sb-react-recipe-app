package com.raxrot.back.repository;

import com.raxrot.back.enums.FoodDifficulty;
import com.raxrot.back.enums.UserRole;
import com.raxrot.back.models.Recipe;
import com.raxrot.back.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    RecipeRepository recipeRepository;

    private User author1;
    private User author2;

    @BeforeEach
    void init() {
        author1 = new User(null, "chef1", "pwd", "chef1@mail.com", UserRole.ROLE_USER, null, null, null, null);
        author2 = new User(null, "chef2", "pwd", "chef2@mail.com", UserRole.ROLE_USER, null, null, null, null);
        em.persist(author1);
        em.persist(author2);

        Recipe r1 = new Recipe(null, "Pancakes", "desc1", "ing1", null, FoodDifficulty.EASY, null, null, author1, new ArrayList<>());
        Recipe r2 = new Recipe(null, "Burger",   "desc2", "ing2", null, FoodDifficulty.MEDIUM, null, null, author1, new ArrayList<>());
        Recipe r3 = new Recipe(null, "Souffle",  "desc3", "ing3", null, FoodDifficulty.HARD, null, null, author2, new ArrayList<>());

        em.persist(r1);
        em.persist(r2);
        em.persist(r3);

        em.flush();
        em.clear();
    }

    @Test
    void findByDifficulty_shouldReturnAllMatching() {
        List<Recipe> easyRecipes = recipeRepository.findByDifficulty(FoodDifficulty.EASY);

        assertThat(easyRecipes).hasSize(1);
        assertThat(easyRecipes).extracting(Recipe::getTitle)
                .containsExactly("Pancakes");
    }

    @Test
    void findByAuthor_shouldReturnAllMatching() {
        List<Recipe> recipesByAuthor1 = recipeRepository.findByAuthor(author1);
        List<Recipe> recipesByAuthor2 = recipeRepository.findByAuthor(author2);

        assertThat(recipesByAuthor1).hasSize(2);
        assertThat(recipesByAuthor1).extracting(Recipe::getTitle)
                .containsExactlyInAnyOrder("Burger", "Pancakes");

        assertThat(recipesByAuthor2).hasSize(1);
        assertThat(recipesByAuthor2).extracting(Recipe::getTitle)
                .containsExactly("Souffle");
    }

    @Test
    void findByAuthorId_shouldReturnAllMatching() {
        List<Recipe> recipesByAuthor1 = recipeRepository.findByAuthor_Id(author1.getId());
        List<Recipe> recipesByAuthor2 = recipeRepository.findByAuthor_Id(author2.getId());

        assertThat(recipesByAuthor1).hasSize(2);
        assertThat(recipesByAuthor1).extracting(Recipe::getTitle)
                .containsExactlyInAnyOrder("Burger", "Pancakes");

        assertThat(recipesByAuthor2).hasSize(1);
        assertThat(recipesByAuthor2).extracting(Recipe::getTitle)
                .containsExactly("Souffle");
    }
}