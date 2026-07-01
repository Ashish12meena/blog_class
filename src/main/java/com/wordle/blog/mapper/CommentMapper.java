package com.wordle.blog.mapper;

import com.wordle.blog.dto.CommentResponseDTO;
import com.wordle.blog.enitity.Comment;
import com.wordle.blog.repository.CommentRepository;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final CommentRepository commentRepository;

    public CommentMapper(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponseDTO toResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .postId(comment.getPost().getId())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .replyCount(commentRepository.countByParentComment_Id(comment.getId()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}