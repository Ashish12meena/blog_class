package com.wordle.blog.enitity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.wordle.blog.enums.StorageType;

/**
 * Represents a single uploaded file (profile image, post attachment, etc.)
 * regardless of WHERE it physically lives (local disk or S3).
 *
 * Why a dedicated Media entity instead of just a "profileImage: String" column on User?
 * - A plain String URL tells you nothing about HOW to delete that file later,
 *   how big it is, what type it is, or which storage backend it lives on.
 * - Centralizing all uploads in one table means any entity (User, Post, etc.)
 *   can reference media the same way, and storage logic lives in exactly one place.
 */
@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The actual file name as it exists in the storage backend
     * (a random UUID-prefixed name, e.g. "550e8400..._photo.jpg").
     *
     * We do NOT trust or store the user's raw uploaded file name as the
     * primary identifier — two users could upload files with identical
     * names ("photo.jpg") and collide. This field is what we generated,
     * not what the user gave us.
     *
     * Currently this duplicates `storageKey` for the local strategy. We keep
     * both fields rather than removing one because they answer two
     * conceptually different questions (see storageKey's comment below) and
     * future storage strategies may need them to diverge.
     */
    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    /**
     * The file name AS THE USER UPLOADED IT (e.g. "vacation photo.jpg").
     *
     * We keep this purely for display purposes — e.g. showing "Download
     * vacation_photo.jpg" in a UI, or logging something a human recognizes.
     * It is never used to build a file path or S3 key (see sanitize() in the
     * storage strategies for why: raw user input should never directly touch
     * a filesystem path).
     */
    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    /**
     * MIME type (e.g. "image/png", "image/jpeg").
     *
     * Needed so that when this file is served back, the browser knows how to
     * render it. Also used by the S3 strategy when uploading (S3 stores this
     * as part of the object's metadata).
     */
    @Column(name = "content_type", nullable = false)
    private String contentType;

    /**
     * Size in bytes at upload time.
     *
     * Why store this instead of just asking the storage backend "how big is
     * this file" when needed? Because that means a network call (S3) or disk
     * I/O (local) every single time you want to show file size — e.g. in an
     * admin dashboard listing 100 uploads. Storing it once at upload time is
     * far cheaper, since file size never changes after upload.
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * Which backend this specific file was stored in (LOCAL or S3).
     *
     * This is critical, not cosmetic: when we later need to DELETE this file,
     * we must know which strategy's delete() method to call. Without this
     * field, deleting media would require trying every backend and hoping
     * one of them has the file — fragile and slow.
     *
     * Also explains why this field matters even if your app only EVER uses
     * one strategy at a time (see @ConditionalOnProperty design below) — if
     * you ever migrate from LOCAL to S3, old rows still say LOCAL, and new
     * rows say S3. The column preserves history correctly across that change.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

    /**
     * The publicly retrievable URL — what gets sent back to the frontend and
     * embedded in <img src="..."> tags.
     *
     * This is the ONLY field a client ever needs. Everything else on this
     * entity is internal bookkeeping the client never sees.
     */
    @Column(nullable = false, length = 1024)
    private String url;

    /**
     * The internal key/path used to physically locate (and delete) this file
     * within its storage backend.
     *
     * Why is this a SEPARATE field from `url` instead of deriving the key by
     * stripping the base URL prefix off the URL at delete time?
     *
     * Because that derivation is fragile: if `baseUrl` ever changes (new CDN
     * domain, bucket renamed, custom domain added), every previously stored
     * URL becomes impossible to parse back into a valid key. By storing the
     * key directly at creation time, deletion never depends on string
     * manipulation of a value that could change independently later.
     *
     * Rule of thumb worth remembering: if recomputing a value later is
     * fragile, store it redundantly instead of deriving it on demand.
     */
    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}