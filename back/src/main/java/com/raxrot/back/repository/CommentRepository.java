package com.raxrot.back.repository;

import com.raxrot.back.models.Comment;
import com.raxrot.back.models.Recipe;
import com.raxrot.back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRecipe(Recipe recipe);
    List<Comment> findByUser(User user);
}
