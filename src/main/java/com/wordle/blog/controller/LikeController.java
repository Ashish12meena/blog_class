package com.wordle.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.blog.dto.LikeCheckResponseDTO;
import com.wordle.blog.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private LikeService likeService;

    @GetMapping("/is-liked")
    public ResponseEntity<LikeCheckResponseDTO> isLiked(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        return ResponseEntity.ok(likeService.isLiked(userId, postId));
    }
}
