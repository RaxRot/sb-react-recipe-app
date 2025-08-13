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
import com.raxrot.back.services.CommentService;
import com.raxrot.back.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final ModelMapper modelMapper;
    private final AuthUtils authUtils;

    public CommentServiceImpl(CommentRepository commentRepository,
                              RecipeRepository recipeRepository,
                              ModelMapper modelMapper,
                              AuthUtils authUtils) {
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.modelMapper = modelMapper;
        this.authUtils = authUtils;
    }

    @Override
    public CommentResponse createComment(Long recipeId, CommentRequest commentRequest) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ApiException("Recipe not found", HttpStatus.NOT_FOUND));

        User user = authUtils.getCurrentUser();

        Comment comment = modelMapper.map(commentRequest, Comment.class);
        comment.setRecipe(recipe);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);

        CommentResponse resp = new CommentResponse();
        resp.setId(saved.getId());
        resp.setComment(saved.getComment());
        resp.setAuthorId(saved.getUser().getId());
        resp.setAuthorUsername(saved.getUser().getUsername());
        resp.setRecipeId(saved.getRecipe().getId());
        return resp;
    }

    @Override
    public List<CommentResponse> getCommentsByRecipeId(Long recipeId) {
        List<Comment> comments = commentRepository.findByRecipeId(recipeId);

        return comments.stream().map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setComment(comment.getComment());
            response.setAuthorId(comment.getUser().getId());
            response.setAuthorUsername(comment.getUser().getUsername());
            response.setRecipeId(comment.getRecipe().getId());
            return response;
        }).toList();
    }

    @Override
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);

        return comments.stream().map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setComment(comment.getComment());
            response.setAuthorId(comment.getUser().getId());
            response.setAuthorUsername(comment.getUser().getUsername());
            response.setRecipeId(comment.getRecipe().getId());
            return response;
        }).toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Comment not found", HttpStatus.NOT_FOUND));

        User me = authUtils.getCurrentUser();
        boolean isOwner = comment.getUser().getId().equals(me.getId());
        boolean isAdmin = me.getRole() == UserRole.ROLE_ADMIN;

        if (isOwner || isAdmin) {
            commentRepository.delete(comment);
        } else {
            throw new ApiException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }
}