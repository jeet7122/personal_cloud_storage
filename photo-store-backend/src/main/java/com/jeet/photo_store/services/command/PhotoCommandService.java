package com.jeet.photo_store.services.command;

import com.jeet.photo_store.dtos.PhotoResponseDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface PhotoCommandService {
    PhotoResponseDto uploadPhoto(MultipartFile file);
    List<PhotoResponseDto> uploadPhotos(MultipartFile[] files);
    void deletePhoto(UUID id);
}
