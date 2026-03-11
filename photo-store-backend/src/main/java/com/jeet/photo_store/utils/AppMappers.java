package com.jeet.photo_store.utils;

import com.jeet.photo_store.dtos.PhotoResponseDto;
import com.jeet.photo_store.models.Photo;
import com.jeet.photo_store.services.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppMappers {
    private final StorageService storageService;

    public PhotoResponseDto mapToPhotoResponse(Photo photo, boolean duplicate){
        return PhotoResponseDto.builder()
                .id(photo.getId())
                .originalFileName(photo.getOriginalFileName())
                .contentType(photo.getContentType())
                .size(photo.getSize())
                .uploadedAt(photo.getUploadedAt())
                .duplicate(duplicate)
                .previewUrl(storageService.generatePresignedUrl(photo.getObjectKey()))
                .build();
    }
}
