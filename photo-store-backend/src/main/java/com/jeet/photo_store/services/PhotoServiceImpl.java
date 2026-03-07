package com.jeet.photo_store.services;

import com.jeet.photo_store.dtos.PhotoResponseDto;
import com.jeet.photo_store.dtos.PhotoUrlResponseDto;
import com.jeet.photo_store.models.Photo;
import com.jeet.photo_store.repository.PhotoRepository;
import com.jeet.photo_store.utils.FileHashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final StorageService storageService;
    private final FileHashUtils fileHashUtils;
    private final FileValidationService fileValidationService;

    @Override
    public List<PhotoResponseDto> getAllPhotos() {
        return photoRepository.findAll().stream()
                .map(photo -> mapToPhotoResponse(photo, false))
                .toList();
    }

    @Override
    public Photo getPhotoById(UUID id) {
        Photo photo = photoRepository.findById(id).orElse(null);

        if (photo == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found by id: " + id);
        }

        return photo;
    }

    @Override
    public PhotoResponseDto uploadPhoto(MultipartFile file) {
        fileValidationService.validateImage(file);

        String hash = fileHashUtils.sha256(file);

        Photo existingPhoto = photoRepository.findByFileHash(hash).orElse(null);
        if (existingPhoto != null){
            return mapToPhotoResponse(existingPhoto, true);
        }

        String objectKey = storageService.uploadFile(file);

        Photo photo = Photo.builder()
                .originalFileName(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename())
                .objectKey(objectKey)
                .contentType(file.getContentType())
                .size(file.getSize())
                .fileHash(hash)
                .build();

        Photo uploadedPhoto = photoRepository.save(photo);
        return mapToPhotoResponse(uploadedPhoto, false);
    }

    @Override
    public PhotoUrlResponseDto getPhotoUrl(UUID id) {
        Photo photo = getPhotoById(id);
        String url = storageService.generatePresignedUrl(photo.getObjectKey());
        return PhotoUrlResponseDto.builder()
                .url(url)
                .build();
    }


    private PhotoResponseDto mapToPhotoResponse(Photo photo, boolean duplicate){
        return PhotoResponseDto.builder()
                .id(photo.getId())
                .originalFileName(photo.getOriginalFileName())
                .contentType(photo.getContentType())
                .size(photo.getSize())
                .uploadedAt(photo.getUploadedAt())
                .duplicate(duplicate)
                .build();
    }
}
