// package com.wordle.blog.strategy;

// import com.wordle.blog.enums.StorageType;
// import com.wordle.blog.exception.MediaUploadException;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.web.multipart.MultipartFile;
// import software.amazon.awssdk.core.sync.RequestBody;
// import software.amazon.awssdk.services.s3.S3Client;
// import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
// import software.amazon.awssdk.services.s3.model.PutObjectRequest;

// import java.io.IOException;
// import java.util.UUID;

// /**
//  * Stores files in an AWS S3 bucket.
//  *
//  * Also a plain, unconditional @Component — same reasoning as
//  * LocalStorageStrategy. Both this class and LocalStorageStrategy are alive
//  * in the Spring context at the same time; the resolver is what decides
//  * which one actually gets used for any given upload/delete call.
//  */
// @Slf4j
// @Component
// public class S3StorageStrategy implements StorageStrategy {

//     private final S3Client s3Client;

//     @Value("${media.s3.bucket-name}")
//     private String bucketName;

//     @Value("${media.s3.base-url}")
//     private String baseUrl;

//     public S3StorageStrategy(S3Client s3Client) {
//         this.s3Client = s3Client;
//     }

//     @Override
//     public StorageResult upload(MultipartFile file) {
//         // Same UUID + sanitize formula as LocalStorageStrategy — intentional.
//         // Both strategies must guarantee unique, safe keys; only WHERE the
//         // bytes get written differs.
//         String key = UUID.randomUUID() + "_" + sanitize(file.getOriginalFilename());

//         try {
//             PutObjectRequest request = PutObjectRequest.builder()
//                     .bucket(bucketName)
//                     .key(key)
//                     .contentType(file.getContentType())
//                     .build();

//             s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//             log.info("Uploaded file to S3 bucket {} with key {}", bucketName, key);

//             String url = baseUrl + "/" + key;
//             return new StorageResult(url, key);

//         } catch (IOException e) {
//             log.error("Failed to upload file to S3: {}", file.getOriginalFilename(), e);
//             throw new MediaUploadException("Failed to upload file to S3", e);
//         } catch (Exception e) {
//             log.error("S3 upload error for file {}", file.getOriginalFilename(), e);
//             throw new MediaUploadException("S3 upload failed", e);
//         }
//     }

//     @Override
//     public void delete(String storageKey) {
//         try {
//             s3Client.deleteObject(DeleteObjectRequest.builder()
//                     .bucket(bucketName)
//                     .key(storageKey)
//                     .build());
//             log.info("Deleted S3 object with key {}", storageKey);
//         } catch (Exception e) {
//             log.error("Failed to delete S3 object with key {}", storageKey, e);
//             throw new MediaUploadException("Failed to delete file from S3", e);
//         }
//     }

//     @Override
//     public StorageType getStorageType() {
//         return StorageType.S3;
//     }

//     private String sanitize(String fileName) {
//         if (fileName == null) return "file";
//         return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
//     }
// }