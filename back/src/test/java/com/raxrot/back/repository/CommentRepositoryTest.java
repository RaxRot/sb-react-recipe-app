package com.raxrot.back.repository;

import com.raxrot.back.enums.FoodDifficulty;
import com.raxrot.back.enums.UserRole;
import com.raxrot.back.models.Comment;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired CommentRepository commentRepository;

    private User u1;
    private User u2;
    private Recipe r1;
    private Recipe r2;

    @BeforeEach
    void seed() {
        u1 = new User(null, "alice", "pwd", "alice@mail.com", UserRole.ROLE_USER, null, null, null, null);
        u2 = new User(null, "bob",   "pwd", "bob@mail.com",   UserRole.ROLE_USER, null, null, null, null);
        em.persist(u1);
        em.persist(u2);

        r1 = new Recipe(null, "Pasta", "desc", "ing", null, FoodDifficulty.MEDIUM, null, null, u1);
        r2 = new Recipe(null, "Soup",  "desc", "ing", null, FoodDifficulty.EASY,   null, null, u2);
        em.persist(r1);
        em.persist(r2);

        Comment c1 = new Comment(null, "nice", null, null, u1, r1);
        Comment c2 = new Comment(null, "wow",  null, null, u2, r1);
        Comment c3 = new Comment(null, "ok",   null, null, u1, r2);
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);

        em.flush();
        em.clear();
    }

    @Test
    void findByRecipe_shouldReturnAllForRecipe() {
        List<Comment> forR1 = commentRepository.findByRecipe(r1);
        List<Comment> forR2 = commentRepository.findByRecipe(r2);

        assertThat(forR1).hasSize(2);
        assertThat(forR1).extracting(Comment::getComment).containsExactlyInAnyOrder("nice", "wow");

        assertThat(forR2).hasSize(1);
        assertThat(forR2.get(0).getComment()).isEqualTo("ok");
    }

    @Test
    void findByUser_shouldReturnAllForUser() {
        List<Comment> forU1 = commentRepository.findByUser(u1);
        List<Comment> forU2 = commentRepository.findByUser(u2);

        assertThat(forU1).hasSize(2);
        assertThat(forU2).hasSize(1);
    }

    @Test
    void findByRecipe_paged_shouldWork() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());
        Page<Comment> page = commentRepository.findByRecipe(r1, pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Comment::getComment)
                .containsExactlyInAnyOrder("nice", "wow");
    }
}