package com.wordle.blog.mapper;

import com.wordle.blog.dto.MediaResponseDTO;
import com.wordle.blog.enitity.Media;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {
    public MediaResponseDTO toResponseDTO(Media media) {
        return MediaResponseDTO.builder()
                .id(media.getId())
                .originalFileName(media.getOriginalFileName())
                .contentType(media.getContentType())
                .fileSize(media.getFileSize())
                .storageType(media.getStorageType())
                .url(media.getUrl())
                .uploadedAt(media.getUploadedAt())
                .build();
    }
}