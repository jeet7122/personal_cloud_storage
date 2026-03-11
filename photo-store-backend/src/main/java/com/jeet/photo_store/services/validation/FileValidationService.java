package com.jeet.photo_store.services.validation;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidationService {
    void validateImage(MultipartFile file);
}
