package com.wordle.blog.controller;

import com.wordle.blog.dto.*;
import com.wordle.blog.service.FollowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<FollowResponseDTO> follow(@Valid @RequestBody FollowRequestDto request) {
        return new ResponseEntity<>(followService.follow(request), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> unfollow(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        followService.unfollow(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/is-following")
    public ResponseEntity<FollowCheckResponseDTO> isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        return ResponseEntity.ok(followService.isFollowing(followerId, followingId));
    }

    // Accounts that {userId} follows
    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<FollowUserSummaryDTO>> getFollowing(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowing(userId, pageable));
    }

    // Accounts that follow {userId}
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<FollowUserSummaryDTO>> getFollowers(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<FollowCountResponseDTO> getFollowCounts(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowCounts(userId));
    }
}