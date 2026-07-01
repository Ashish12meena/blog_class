package com.wordle.blog.controller;

import com.wordle.blog.dto.*;
import com.wordle.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CreateCommentRequestDto request) {
        return new ResponseEntity<>(commentService.createComment(request), HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdateCommentRequestDto request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, userId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // top-level comments for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDTO>> getTopLevelComments(
            @PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getTopLevelComments(postId, pageable));
    }

    // replies for a specific comment
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponseDTO>> getReplies(
            @PathVariable Long commentId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getReplies(commentId, pageable));
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<CommentCountResponseDTO> getCommentCount(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentCount(postId));
    }
}