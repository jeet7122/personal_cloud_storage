package com.jeet.photo_store.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidationService {
    void validateImage(MultipartFile file);
}
