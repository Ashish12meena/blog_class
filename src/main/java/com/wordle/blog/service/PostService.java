package com.wordle.blog.service;

import com.wordle.blog.dto.CreatePostRequestDto;
import com.wordle.blog.dto.PostCountResponseDTO;
import com.wordle.blog.dto.PostResponseDTO;
import com.wordle.blog.dto.UpdatePostRequestDto;
import com.wordle.blog.enitity.Post;
import com.wordle.blog.enitity.User;
import com.wordle.blog.enums.PostStatus;
import com.wordle.blog.exception.InvalidPostStatusTransitionException;
import com.wordle.blog.exception.PostNotFoundException;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.mapper.PostMapper;
import com.wordle.blog.repository.PostRepository;
import com.wordle.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository,
            UserRepository userRepository,
            PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    @Transactional
    public PostResponseDTO createPost(CreatePostRequestDto request) {
        User author = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new UserNotFoundException("User not found at id: " + request.getCreatedById()));

        Post post = postMapper.toEntity(request, author);
        Post saved = postRepository.save(post);
        log.info("Created post with id {} by user {}", saved.getId(), author.getId());

        return postMapper.toResponseDTO(saved);
    }

    @Transactional
    public PostResponseDTO updatePost(Long id, UpdatePostRequestDto request) {
        Post post = findEntityById(id);

        if (StringUtils.hasText(request.getTitle())) {
            post.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            post.setContent(request.getContent());
        }

        Post saved = postRepository.save(post);
        log.info("Updated post with id {}", saved.getId());

        return postMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = findEntityById(id);
        post.setDeletedAt(java.time.LocalDateTime.now());
        postRepository.save(post);
        log.info("Soft-deleted post with id {}", id);
    }

    public Page<PostResponseDTO> getAllPostsByUser(Long userId, Pageable pageable) {
        return postRepository.findByCreatedBy_Id(userId, pageable)
                .map(postMapper::toResponseDTO);
    }

    public PostCountResponseDTO getPostCountOfUser(Long userId) {
        long count = postRepository.countByCreatedBy_Id(userId);
        return PostCountResponseDTO.builder()
                .userId(userId)
                .postCount(count)
                .build();
    }

    public Page<PostResponseDTO> getAllArchivedPosts(Pageable pageable) {
        return postRepository.findByStatus(PostStatus.ARCHIVED, pageable)
                .map(postMapper::toResponseDTO);
    }

    public Page<PostResponseDTO> getAllDraftPostsOfUser(Long userId, Pageable pageable) {
        return postRepository.findByCreatedBy_IdAndStatus(userId, PostStatus.DRAFT, pageable)
                .map(postMapper::toResponseDTO);
    }

    public Page<PostResponseDTO> getPostsByCategory(Long categoryId, Pageable pageable) {
        return postRepository.findByCategoryId(categoryId, pageable)
                .map(postMapper::toResponseDTO);
    }

    public PostResponseDTO getPostById(Long id) {
        return postMapper.toResponseDTO(findEntityById(id));
    }

    @Transactional
    public PostResponseDTO archivePost(Long id) {
        Post post = findEntityById(id);

        if (post.getStatus() == PostStatus.ARCHIVED) {
            log.warn("Attempt to archive post {} which is already archived", id);
            throw new InvalidPostStatusTransitionException("Post is already archived");
        }

        post.setStatus(PostStatus.ARCHIVED);
        Post saved = postRepository.save(post);
        log.info("Archived post with id {}", saved.getId());

        return postMapper.toResponseDTO(saved);
    }

    @Transactional
    public PostResponseDTO unArchivePost(Long id) {
        Post post = findEntityById(id);

        if (post.getStatus() != PostStatus.ARCHIVED) {
            log.warn("Attempt to unarchive post {} which is not archived (current status: {})", id, post.getStatus());
            throw new InvalidPostStatusTransitionException("Post is not currently archived");
        }

        post.setStatus(PostStatus.PUBLISHED);
        Post saved = postRepository.save(post);
        log.info("Unarchived post with id {}, set to PUBLISHED", saved.getId());

        return postMapper.toResponseDTO(saved);
    }

    private Post findEntityById(Long id) {
        return postRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn("Post not found or deleted for id {}", id);
                    return new PostNotFoundException("Post not found at id: " + id);
                });
    }
}