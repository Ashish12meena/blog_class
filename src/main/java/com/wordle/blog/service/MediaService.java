package com.wordle.blog.service;

import com.wordle.blog.dto.MediaResponseDTO;
import com.wordle.blog.enitity.Media;
import com.wordle.blog.enums.StorageType;
import com.wordle.blog.exception.MediaNotFoundException;
import com.wordle.blog.exception.MediaUploadException;
import com.wordle.blog.mapper.MediaMapper;
import com.wordle.blog.repository.MediaRepository;
import com.wordle.blog.strategy.StorageResult;
import com.wordle.blog.strategy.StorageStrategy;
import com.wordle.blog.strategy.StorageStrategyResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Orchestrates: validate the incoming file -> ask the resolver for the
 * correct StorageStrategy -> delegate the actual storing to it -> persist a
 * Media row recording what happened and where.
 *
 * This class never imports LocalStorageStrategy or S3StorageStrategy
 * directly — only the StorageStrategy interface and the resolver. That
 * decoupling is the entire benefit of the Strategy Pattern.
 */
@Slf4j
@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final StorageStrategyResolver storageStrategyResolver;

    /**
     * The default backend to use when a caller doesn't explicitly specify
     * one. Per-request override is still possible via the second upload()
     * overload below — that's the flexibility this design buys over
     * @ConditionalOnProperty.
     */
    @Value("${media.default-storage-type}")
    private StorageType defaultStorageType;

    public MediaService(MediaRepository mediaRepository,
                         MediaMapper mediaMapper,
                         StorageStrategyResolver storageStrategyResolver) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.storageStrategyResolver = storageStrategyResolver;
    }

    @Transactional
    public MediaResponseDTO upload(MultipartFile file) {
        return upload(file, defaultStorageType);
    }

    /**
     * Lets the caller pick a specific backend for THIS upload only — e.g. a
     * multi-tenant app where Tenant A's uploads always go to S3 regardless
     * of what the global default is. This overload is impossible to express
     * cleanly under the @ConditionalOnProperty design, since only one
     * backend bean would exist there at all.
     */
    @Transactional
    public MediaResponseDTO upload(MultipartFile file, StorageType storageType) {
        if (file == null || file.isEmpty()) {
            log.warn("Upload attempted with empty or missing file");
            throw new MediaUploadException("Uploaded file is empty");
        }

        log.info("Uploading file '{}' ({} bytes) using strategy {}",
                file.getOriginalFilename(), file.getSize(), storageType);

        StorageStrategy strategy = storageStrategyResolver.resolve(storageType);
        StorageResult result = strategy.upload(file);

        Media media = Media.builder()
                .storedFileName(result.getStorageKey())
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .storageType(storageType)
                .url(result.getUrl())
                .storageKey(result.getStorageKey())
                .build();

        Media saved = mediaRepository.save(media);
        log.info("Media record persisted with id {} at url {}", saved.getId(), saved.getUrl());

        return mediaMapper.toResponseDTO(saved);
    }

    public MediaResponseDTO getById(Long id) {
        Media media = findEntityById(id);
        return mediaMapper.toResponseDTO(media);
    }

    @Transactional
    public void deleteById(Long id) {
        Media media = findEntityById(id);

        // We resolve based on the STORED storageType, not the current
        // default — this is important: a file uploaded to S3 last year must
        // still be deleted via the S3 strategy today, even if the app's
        // default has since changed to LOCAL. The Media row is the source
        // of truth for "where does this file actually live."
        StorageStrategy strategy = storageStrategyResolver.resolve(media.getStorageType());
        strategy.delete(media.getStorageKey());

        mediaRepository.delete(media);
        log.info("Deleted media id {} and its underlying file", id);
    }

    private Media findEntityById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Media not found for id {}", id);
                    return new MediaNotFoundException("Media not found at id: " + id);
                });
    }
}