package com.raxrot.back.repository;

import com.raxrot.back.enums.FoodDifficulty;
import com.raxrot.back.models.Recipe;
import com.raxrot.back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByDifficulty(FoodDifficulty difficulty);
    List<Recipe> findByAuthor(User author);
}
