package com.wordle.blog.service;

import com.wordle.blog.dto.LikeCheckResponseDTO;
import com.wordle.blog.dto.LikeCountResponseDTO;
import com.wordle.blog.dto.LikePostRequestDto;
import com.wordle.blog.dto.LikeResponseDTO;
import com.wordle.blog.dto.MostLikedPostResponseDTO;
import com.wordle.blog.enitity.Like;
import com.wordle.blog.enitity.Post;
import com.wordle.blog.enitity.User;
import com.wordle.blog.exception.AlreadyLikedException;
import com.wordle.blog.exception.LikeNotFoundException;
import com.wordle.blog.exception.PostNotFoundException;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.mapper.LikeMapper;
import com.wordle.blog.repository.LikeRepository;
import com.wordle.blog.repository.PostRepository;
import com.wordle.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;

    public LikeService(LikeRepository likeRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeMapper = likeMapper;
    }

    @Transactional
    public LikeResponseDTO likePost(LikePostRequestDto request) {
        if (likeRepository.existsByLikedBy_IdAndPost_Id(request.getUserId(), request.getPostId())) {
            log.warn("User {} attempted to like post {} twice", request.getUserId(), request.getPostId());
            throw new AlreadyLikedException("User has already liked this post");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found at id: " + request.getUserId()));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found at id: " + request.getPostId()));

        Like like = Like.builder()
                .likedBy(user)
                .post(post)
                .build();

        Like saved = likeRepository.save(like);
        log.info("User {} liked post {}", request.getUserId(), request.getPostId());

        return likeMapper.toResponseDTO(saved);
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        Like like = likeRepository.findByLikedBy_IdAndPost_Id(userId, postId)
                .orElseThrow(() -> {
                    log.warn("Unlike attempted but no like found for user {} on post {}", userId, postId);
                    return new LikeNotFoundException("Like not found for this user and post");
                });

        likeRepository.delete(like);
        log.info("User {} unliked post {}", userId, postId);
    }

    public LikeCountResponseDTO getLikeCountByPostId(Long postId) {
        long count = likeRepository.countByPost_Id(postId);
        return LikeCountResponseDTO.builder()
                .postId(postId)
                .likeCount(count)
                .build();
    }

    public List<MostLikedPostResponseDTO> getMostLikedPostsByUser(Long userId) {
        return likeRepository.findMostLikedPostsByUser(userId)
                .stream()
                .map(p -> MostLikedPostResponseDTO.builder()
                        .postId(p.getPostId())
                        .title(p.getTitle())
                        .likeCount(p.getLikeCount())
                        .build())
                .toList();
    }

    public LikeCheckResponseDTO isLiked(Long userId, Long postId) {
        boolean result = likeRepository.existsByLikedBy_IdAndPost_Id(userId, postId);
        log.info("Like check: user {} liked post {} -> {}", userId, postId, result);

        return LikeCheckResponseDTO.builder()
                .userId(userId)
                .postId(postId)
                .liked(result)
                .build();
    }
}