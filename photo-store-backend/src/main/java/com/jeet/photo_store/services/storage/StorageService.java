package com.jeet.photo_store.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);
    String generatePresignedUrl(String objectKey);
    void deleteFile(String objectKey);
}
