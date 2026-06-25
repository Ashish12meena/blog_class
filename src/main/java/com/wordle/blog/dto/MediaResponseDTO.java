package com.wordle.blog.dto;

import com.wordle.blog.enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDTO {
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private StorageType storageType;
    private String url;
    private LocalDateTime uploadedAt;
}