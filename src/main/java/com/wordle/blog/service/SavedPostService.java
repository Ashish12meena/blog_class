package com.wordle.blog.service;

import com.wordle.blog.dto.*;
import com.wordle.blog.enitity.Post;
import com.wordle.blog.enitity.SavedPost;
import com.wordle.blog.enitity.User;
import com.wordle.blog.exception.AlreadyExistsException;
import com.wordle.blog.exception.PostNotFoundException;
import com.wordle.blog.exception.SavedPostNotFoundException;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.mapper.SavedPostMapper;
import com.wordle.blog.repository.PostRepository;
import com.wordle.blog.repository.SavedPostRepository;
import com.wordle.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SavedPostMapper savedPostMapper;

    public SavedPostService(SavedPostRepository savedPostRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            SavedPostMapper savedPostMapper) {
        this.savedPostRepository = savedPostRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.savedPostMapper = savedPostMapper;
    }

    @Transactional
    public SavedPostResponseDTO savePost(SavedPostRequestDto request) {
        if (savedPostRepository.existsByUser_IdAndPost_Id(request.getUserId(), request.getPostId())) {
            log.warn("User {} attempted to save post {} twice", request.getUserId(), request.getPostId());
            throw new AlreadyExistsException("Post is already saved");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found at id: " + request.getUserId()));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found at id: " + request.getPostId()));

        SavedPost savedPost = SavedPost.builder()
                .user(user)
                .post(post)
                .build();

        SavedPost saved = savedPostRepository.save(savedPost);
        log.info("User {} saved post {}", request.getUserId(), request.getPostId());

        return savedPostMapper.toResponseDTO(saved);
    }

    @Transactional
    public void unsavePost(Long userId, Long postId) {
        SavedPost savedPost = savedPostRepository.findByUser_IdAndPost_Id(userId, postId)
                .orElseThrow(() -> {
                    log.warn("Unsave attempted but no saved post found for user {} on post {}", userId, postId);
                    return new SavedPostNotFoundException("Saved post not found for this user and post");
                });

        savedPostRepository.delete(savedPost);
        log.info("User {} unsaved post {}", userId, postId);
    }

    public SavedPostCheckResponseDTO isSaved(Long userId, Long postId) {
        boolean result = savedPostRepository.existsByUser_IdAndPost_Id(userId, postId);
        log.info("Saved-post check: user {} saved post {} -> {}", userId, postId, result);

        return SavedPostCheckResponseDTO.builder()
                .userId(userId)
                .postId(postId)
                .saved(result)
                .build();
    }

    public Page<SavedPostSummaryDTO> getSavedPosts(Long userId, Pageable pageable) {
        return savedPostRepository.findByUser_Id(userId, pageable)
                .map(savedPostMapper::toSummaryDTO);
    }
}