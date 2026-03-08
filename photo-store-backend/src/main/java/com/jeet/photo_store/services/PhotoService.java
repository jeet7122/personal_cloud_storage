package com.jeet.photo_store.services;

import com.jeet.photo_store.dtos.PhotoPageResponseDto;
import com.jeet.photo_store.dtos.PhotoResponseDto;
import com.jeet.photo_store.dtos.PhotoUrlResponseDto;
import com.jeet.photo_store.models.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoService {
    List<PhotoResponseDto> getAllPhotos();
    Photo getPhotoById(UUID id);
    PhotoResponseDto uploadPhoto(MultipartFile file);
    PhotoUrlResponseDto getPhotoUrl(UUID id);
    void deletePhoto(UUID id);
    List<PhotoResponseDto> uploadPhotos(MultipartFile[] files);
    PhotoPageResponseDto getPhotos(int page, int size);
}
