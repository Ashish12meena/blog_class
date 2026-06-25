package com.wordle.blog.strategy;

import com.wordle.blog.enums.StorageType;
import org.springframework.web.multipart.MultipartFile;

/**
 * The Strategy Pattern contract: any class that can "store a file somewhere
 * and later delete it" implements this interface.
 *
 * MediaService talks ONLY to this interface (via the resolver) — it never
 * knows or cares whether the concrete implementation writes to local disk or
 * to S3. Adding a third backend later (e.g. Google Cloud Storage) means
 * writing one new class here and touching nothing else in the codebase.
 */
public interface StorageStrategy {

    /**
     * Uploads the file and returns both the public URL and the internal
     * storage key needed to delete it later.
     */
    StorageResult upload(MultipartFile file);

    /**
     * Deletes a previously uploaded file using its storage key
     * (NOT its URL — see Media.storageKey's comment for why).
     */
    void delete(String storageKey);

    /**
     * Identifies which StorageType this implementation handles. The
     * resolver uses this to build its lookup map at startup.
     */
    StorageType getStorageType();
}