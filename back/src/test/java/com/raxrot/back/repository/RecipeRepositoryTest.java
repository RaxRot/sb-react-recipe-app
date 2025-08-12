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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired RecipeRepository recipeRepository;

    private User author1;
    private User author2;

    @BeforeEach
    void init() {
        author1 = new User(null, "chef1", "pwd", "chef1@mail.com", UserRole.ROLE_USER, null, null, null, null);
        author2 = new User(null, "chef2", "pwd", "chef2@mail.com", UserRole.ROLE_USER, null, null, null, null);
        em.persist(author1);
        em.persist(author2);

        Recipe r1 = new Recipe(null, "Pancakes", "desc1", "ing1", null, FoodDifficulty.EASY, null, null, author1);
        Recipe r2 = new Recipe(null, "Burger",   "desc2", "ing2", null, FoodDifficulty.MEDIUM, null, null, author1);
        Recipe r3 = new Recipe(null, "Souffle",  "desc3", "ing3", null, FoodDifficulty.HARD, null, null, author2);

        em.persist(r1);
        em.persist(r2);
        em.persist(r3);

        em.flush();
        em.clear();
    }

    @Test
    void findByDifficulty_shouldReturnPaged() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Recipe> page = recipeRepository.findByDifficulty(FoodDifficulty.EASY, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).extracting(Recipe::getTitle).containsExactly("Pancakes");
    }

    @Test
    void findByAuthor_shouldReturnPaged() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Recipe> page1 = recipeRepository.findByAuthor(author1, pageable);
        Page<Recipe> page2 = recipeRepository.findByAuthor(author2, pageable);

        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getContent()).extracting(Recipe::getTitle)
                .containsExactly("Burger", "Pancakes");

        assertThat(page2.getTotalElements()).isEqualTo(1);
        assertThat(page2.getContent()).extracting(Recipe::getTitle)
                .containsExactly("Souffle");
    }
}