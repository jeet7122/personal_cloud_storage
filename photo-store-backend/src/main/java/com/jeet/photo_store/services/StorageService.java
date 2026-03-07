package com.jeet.photo_store.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);
    String generatePresignedUrl(String objectKey);
}
