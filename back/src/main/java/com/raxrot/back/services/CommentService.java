package com.raxrot.back.services;

import com.raxrot.back.dtos.CommentRequest;
import com.raxrot.back.dtos.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long recipeId, CommentRequest commentRequest);
    List<CommentResponse> getCommentsByRecipeId(Long recipeId);
    List<CommentResponse> getCommentsByUserId(Long userId);
    void deleteComment(Long commentId);
}
