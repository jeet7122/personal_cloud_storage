package com.jeet.photo_store.services.query;

import com.jeet.photo_store.dtos.PhotoPageResponseDto;
import com.jeet.photo_store.dtos.PhotoUrlResponseDto;
import com.jeet.photo_store.models.Photo;

import java.util.UUID;

public interface PhotoQueryService {
    PhotoPageResponseDto getPhotos(int page, int size);
    PhotoUrlResponseDto getPhotoUrl(UUID id);
    Photo getPhotoById(UUID id);
}
