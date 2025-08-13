package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.CommentRequest;
import com.raxrot.back.dtos.CommentResponse;
import com.raxrot.back.enums.UserRole;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Comment;
import com.raxrot.back.models.Recipe;
import com.raxrot.back.models.User;
import com.raxrot.back.repository.CommentRepository;
import com.raxrot.back.repository.RecipeRepository;
import com.raxrot.back.utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock private CommentRepository commentRepository;
    @Mock private RecipeRepository recipeRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private AuthUtils authUtils;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private User admin;
    private Recipe recipe;
    private CommentRequest req;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "alice", "pwd", "alice@mail.com", UserRole.ROLE_USER, null, null, null, null);
        admin = new User(2L, "bob", "pwd", "bob@mail.com", UserRole.ROLE_ADMIN, null, null, null, null);

        recipe = new Recipe();
        recipe.setId(10L);
        recipe.setAuthor(user);

        req = new CommentRequest();
        req.setComment("nice");

        savedComment = new Comment();
        savedComment.setId(100L);
        savedComment.setComment("nice");
        savedComment.setUser(user);
        savedComment.setRecipe(recipe);
    }

    @Test
    void createComment_shouldMapAndSave() {
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(authUtils.getCurrentUser()).thenReturn(user);

        Comment mapped = new Comment();
        when(modelMapper.map(req, Comment.class)).thenReturn(mapped);

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponse resp = commentService.createComment(10L, req);

        assertThat(resp.getId()).isEqualTo(100L);
        assertThat(resp.getComment()).isEqualTo("nice");
        assertThat(resp.getAuthorId()).isEqualTo(1L);
        assertThat(resp.getAuthorUsername()).isEqualTo("alice");
        assertThat(resp.getRecipeId()).isEqualTo(10L);

        verify(commentRepository).save(mapped);
        assertThat(mapped.getUser()).isEqualTo(user);
        assertThat(mapped.getRecipe()).isEqualTo(recipe);
    }

    @Test
    void createComment_shouldThrow_whenRecipeNotFound() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(99L, req))
                .isInstanceOf(ApiException.class)
                .hasMessage("Recipe not found")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCommentsByRecipeId_shouldReturnMappedList() {
        Comment c1 = new Comment(); c1.setId(1L); c1.setComment("a"); c1.setUser(user); c1.setRecipe(recipe);
        Comment c2 = new Comment(); c2.setId(2L); c2.setComment("b"); c2.setUser(user); c2.setRecipe(recipe);
        when(commentRepository.findByRecipeId(10L)).thenReturn(List.of(c1, c2));

        List<CommentResponse> list = commentService.getCommentsByRecipeId(10L);

        assertThat(list).hasSize(2);
        assertThat(list).extracting(CommentResponse::getComment).containsExactlyInAnyOrder("a", "b");
        assertThat(list).allMatch(r -> r.getAuthorId().equals(1L) && r.getRecipeId().equals(10L));
    }

    @Test
    void getCommentsByUserId_shouldReturnMappedList() {
        Comment c1 = new Comment(); c1.setId(1L); c1.setComment("x"); c1.setUser(user); c1.setRecipe(recipe);
        when(commentRepository.findByUserId(1L)).thenReturn(List.of(c1));

        List<CommentResponse> list = commentService.getCommentsByUserId(1L);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getComment()).isEqualTo("x");
        assertThat(list.get(0).getAuthorUsername()).isEqualTo("alice");
    }

    @Test
    void deleteComment_shouldAllowOwner() {
        Comment c = new Comment(); c.setId(5L); c.setUser(user); c.setRecipe(recipe);
        when(commentRepository.findById(5L)).thenReturn(Optional.of(c));
        when(authUtils.getCurrentUser()).thenReturn(user);

        commentService.deleteComment(5L);

        verify(commentRepository).delete(c);
    }

    @Test
    void deleteComment_shouldAllowAdmin() {
        Comment c = new Comment(); c.setId(6L); c.setUser(user); c.setRecipe(recipe);
        when(commentRepository.findById(6L)).thenReturn(Optional.of(c));
        when(authUtils.getCurrentUser()).thenReturn(admin);

        commentService.deleteComment(6L);

        verify(commentRepository).delete(c);
    }

    @Test
    void deleteComment_shouldThrow_whenNotOwnerAndNotAdmin() {
        User stranger = new User(3L, "kate", "pwd", "k@mail.com", UserRole.ROLE_USER, null, null, null, null);
        Comment c = new Comment(); c.setId(7L); c.setUser(user); c.setRecipe(recipe);

        when(commentRepository.findById(7L)).thenReturn(Optional.of(c));
        when(authUtils.getCurrentUser()).thenReturn(stranger);

        assertThatThrownBy(() -> commentService.deleteComment(7L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Forbidden")
                .extracting("status").isEqualTo(HttpStatus.FORBIDDEN);

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_shouldThrow_whenNotFound() {
        when(commentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(404L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Comment not found")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }
}