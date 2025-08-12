package com.raxrot.back.repository;

import com.raxrot.back.enums.FoodDifficulty;
import com.raxrot.back.models.Recipe;
import com.raxrot.back.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByDifficulty(FoodDifficulty difficulty, Pageable pageable);
    Page<Recipe> findByAuthor(User author, Pageable pageable);
}
