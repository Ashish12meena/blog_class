package com.wordle.blog.service;

import com.wordle.blog.dto.*;
import com.wordle.blog.enitity.Comment;
import com.wordle.blog.enitity.Post;
import com.wordle.blog.enitity.User;
import com.wordle.blog.exception.CommentNotFoundException;
import com.wordle.blog.exception.PostNotFoundException;
import com.wordle.blog.exception.UnauthorizedActionException;
import com.wordle.blog.exception.UserNotFoundException;
import com.wordle.blog.mapper.CommentMapper;
import com.wordle.blog.repository.CommentRepository;
import com.wordle.blog.repository.PostRepository;
import com.wordle.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public CommentResponseDTO createComment(CreateCommentRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found at id: " + request.getUserId()));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found at id: " + request.getPostId()));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException(
                            "Parent comment not found at id: " + request.getParentCommentId()));
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);

        // keep Post.commentCount in sync atomically -> needed by feed scoring
        postRepository.incrementCommentCount(post.getId());

        log.info("User {} commented on post {} (parent: {})", request.getUserId(), request.getPostId(),
                request.getParentCommentId());

        return commentMapper.toResponseDTO(saved);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long commentId, Long userId, UpdateCommentRequestDto request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found at id: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to edit comment {} owned by user {}", userId, commentId,
                    comment.getUser().getId());
            throw new UnauthorizedActionException("You can only edit your own comments");
        }

        comment.setContent(request.getContent());
        Comment saved = commentRepository.save(comment);

        return commentMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found at id: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to delete comment {} owned by user {}", userId, commentId,
                    comment.getUser().getId());
            throw new UnauthorizedActionException("You can only delete your own comments");
        }

        Long postId = comment.getPost().getId();

        // cascade = ALL on replies means deleting a parent removes its replies too;
        // decrement once for the parent (replies don't count toward Post.commentCount
        // unless you want every reply counted - adjust if your scoring needs that)
        commentRepository.delete(comment);
        postRepository.decrementCommentCount(postId);

        log.info("User {} deleted comment {}", userId, commentId);
    }

    public Page<CommentResponseDTO> getTopLevelComments(Long postId, Pageable pageable) {
        return commentRepository.findTopLevelByPostId(postId, pageable)
                .map(commentMapper::toResponseDTO);
    }

    public Page<CommentResponseDTO> getReplies(Long parentCommentId, Pageable pageable) {
        return commentRepository.findByParentComment_IdOrderByCreatedAtAsc(parentCommentId, pageable)
                .map(commentMapper::toResponseDTO);
    }

    public CommentCountResponseDTO getCommentCount(Long postId) {
        long count = commentRepository.countByPost_Id(postId);
        return CommentCountResponseDTO.builder()
                .postId(postId)
                .commentCount(count)
                .build();
    }
}