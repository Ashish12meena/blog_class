package com.wordle.blog.controller;

import com.wordle.blog.dto.*;
import com.wordle.blog.service.SavedPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saved-posts")
@RequiredArgsConstructor
public class SavedPostController {

    private final SavedPostService savedPostService;

    @PostMapping
    public ResponseEntity<SavedPostResponseDTO> savePost(@Valid @RequestBody SavedPostRequestDto request) {
        return new ResponseEntity<>(savedPostService.savePost(request), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> unsavePost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        savedPostService.unsavePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/is-saved")
    public ResponseEntity<SavedPostCheckResponseDTO> isSaved(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        return ResponseEntity.ok(savedPostService.isSaved(userId, postId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<SavedPostSummaryDTO>> getSavedPosts(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(savedPostService.getSavedPosts(userId, pageable));
    }
}