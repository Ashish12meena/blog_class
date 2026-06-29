package com.wordle.blog.controller;

import com.wordle.blog.dto.MediaResponseDTO;
import com.wordle.blog.enums.StorageType;
import com.wordle.blog.service.MediaService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * The optional "storageType" request param is what the resolver design
 * enables: a client CAN override the backend per upload. If omitted, the
 * service falls back to media.default-storage-type from application.yaml.
 */
@Slf4j
@RestController
@RequestMapping("api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MediaResponseDTO> upload(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam(value = "storageType", required = false) StorageType storageType) {

        log.info("Received upload request for file '{}'", file.getOriginalFilename());

        MediaResponseDTO response = storageType == null ? mediaService.upload(file) : mediaService.upload(file, storageType);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        mediaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}