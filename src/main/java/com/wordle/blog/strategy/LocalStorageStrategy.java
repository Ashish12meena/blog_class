package com.wordle.blog.strategy;

import com.wordle.blog.dto.LoadedFile;
import com.wordle.blog.enums.StorageType;
import com.wordle.blog.exception.MediaNotFoundException;
import com.wordle.blog.exception.MediaUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// ̥
//  * Stores files on the local filesystem.
//  *
//  * This is a plain @Component — no conditional. It is ALWAYS registered as a
//  * bean alongside S3StorageStrategy. Spring collects every StorageStrategy
//  * bean into a List and hands that list to StorageStrategyResolver, which
//  * decides at REQUEST TIME which one to actually use. This is what makes
//  * runtime, per-request backend selection possible — both implementations
//  * exist in memory simultaneously, ready to be picked.
//  *
@Slf4j
@Component
@Primary
public class LocalStorageStrategy implements StorageStrategy , LoadLocalStorage {

    @Value("${media.local.base-path}")
    private String basePath; // ./upload/media

    @Value("${media.local.base-url}")
    private String baseUrl; // http://localhost:8080/media

    @Override
    public StorageResult upload(MultipartFile file) {
        try {
            Path uploadDir = Paths.get(basePath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Created local media directory at {}", uploadDir.toAbsolutePath());
            }

            // UUID prefix guarantees uniqueness even if two users upload a
            // file with the exact same original name at the exact same time.
            String storedFileName = UUID.randomUUID() + "_" + sanitize(file.getOriginalFilename());
            Path targetPath = uploadDir.resolve(storedFileName);

            file.transferTo(targetPath);
            log.info("Stored file locally at {}", targetPath.toAbsolutePath());

            // url = baseUrl + key. We return BOTH separately rather than
            // making the caller strip baseUrl off the url later (see
            // Media.storageKey's comment for the full reasoning).
            String url = baseUrl + "/" + storedFileName;
            return new StorageResult(url, storedFileName);

        } catch (IOException e) {
            log.error("Failed to store file locally: {}", file.getOriginalFilename(), e);
            throw new MediaUploadException("Failed to store file on local disk", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Path target = Paths.get(basePath).resolve(storageKey);
            boolean deleted = Files.deleteIfExists(target);
            log.info("Local file delete for key {} -> {}", storageKey, deleted ? "deleted" : "not found");
        } catch (IOException e) {
            log.error("Failed to delete local file with key {}", storageKey, e);
            throw new MediaUploadException("Failed to delete file from local disk", e);
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.LOCAL;
    }

    /**
     * Strips out anything that isn't a letter, digit, dot, underscore, or
     * hyphen from a user-supplied file name.
     *
     * Two reasons this matters: (1) spaces/special characters in filenames
     * can break URLs, and (2) it's a guard against path traversal — if a
     * malicious filename contained "../../etc/passwd", the slashes are
     * replaced before that string is ever used to build a real file path.
     * Never trust a client-supplied filename as-is when constructing a
     * server-side path.
     */
    private String sanitize(String fileName) {
        if (fileName == null)
            return "file";
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Override
    public LoadedFile loadFile(String filename) {
        Path baseDir = Paths.get(basePath).toAbsolutePath().normalize();
        Path filePath = baseDir.resolve(filename).normalize();

        if (!filePath.startsWith(baseDir)) {
            log.warn("Rejected path traversal attempt for filename '{}'", filename);
            throw new MediaNotFoundException("Invalid file path: " + filename);
        }

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new MediaNotFoundException("Invalid file path: " + filename);
        }

        if (!resource.exists() || !resource.isReadable()) {
            log.warn("Requested local file not found or unreadable: '{}'", filename);
            throw new MediaNotFoundException("File not found: " + filename);
        }

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        

        return new LoadedFile(resource, contentType);
    }

    
}