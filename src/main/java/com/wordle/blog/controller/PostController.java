package com.wordle.blog.controller;

import com.wordle.blog.dto.CreatePostRequestDto;
import com.wordle.blog.dto.PostCountResponseDTO;
import com.wordle.blog.dto.PostResponseDTO;
import com.wordle.blog.dto.UpdatePostRequestDto;
import com.wordle.blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // public PostController(PostService postService) {
    // this.postService = postService;
    // }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody CreatePostRequestDto request) {
        log.info("Received create post request from user {}", request.getCreatedById());
        PostResponseDTO response = postService.createPost(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequestDto request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDTO>> getAllPostsByUser(
            @PathVariable Long userId,
            @RequestParam int pageno,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(pageno, size);
        return ResponseEntity.ok(postService.getAllPostsByUser(userId, pageable));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<PostCountResponseDTO> getPostCountOfUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostCountOfUser(userId));
    }

    @GetMapping("/archived")
    public ResponseEntity<Page<PostResponseDTO>> getAllArchivedPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllArchivedPosts(pageable));
    }

    @GetMapping("/user/{userId}/drafts")
    public ResponseEntity<Page<PostResponseDTO>> getAllDraftPostsOfUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getAllDraftPostsOfUser(userId, pageable));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<PostResponseDTO> archivePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.archivePost(id));
    }

    @PatchMapping("/{id}/unarchive")
    public ResponseEntity<PostResponseDTO> unArchivePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.unArchivePost(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam int size,
            @RequestParam int pageno) {

        Pageable pageable = PageRequest.of(pageno, size);
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }
}