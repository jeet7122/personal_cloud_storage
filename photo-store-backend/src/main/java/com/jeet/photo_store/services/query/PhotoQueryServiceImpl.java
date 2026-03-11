package com.jeet.photo_store.services.query;

import com.jeet.photo_store.dtos.PhotoPageResponseDto;
import com.jeet.photo_store.dtos.PhotoResponseDto;
import com.jeet.photo_store.dtos.PhotoUrlResponseDto;
import com.jeet.photo_store.models.Photo;
import com.jeet.photo_store.repository.PhotoRepository;
import com.jeet.photo_store.services.storage.StorageService;
import com.jeet.photo_store.utils.AppMappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoQueryServiceImpl implements PhotoQueryService{

    private final PhotoRepository photoRepository;
    private final AppMappers appMappers;
    private final StorageService storageService;

    @Override
    public PhotoPageResponseDto getPhotos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        Page<Photo> photoPage = photoRepository.findAll(pageable);

        List<PhotoResponseDto> photos = photoPage.getContent()
                .stream()
                .map(photo -> appMappers.mapToPhotoResponse(photo, false))
                .toList();

        return PhotoPageResponseDto.builder()
                .content(photos)
                .page(photoPage.getNumber())
                .size(photoPage.getSize())
                .totalPages(photoPage.getTotalPages())
                .totalElements(photoPage.getTotalElements())
                .hasNext(photoPage.hasNext())
                .hasPrevious(photoPage.hasPrevious())
                .build();
    }

    @Override
    public PhotoUrlResponseDto getPhotoUrl(UUID id) {
        Photo photo = getPhotoById(id);
        String url = storageService.generatePresignedUrl(photo.getObjectKey());
        return PhotoUrlResponseDto.builder()
                .url(url)
                .build();
    }

    @Override
    public Photo getPhotoById(UUID id) {
        Photo photo = photoRepository.findById(id).orElse(null);

        if (photo == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found by id: " + id);
        }

        return photo;
    }
}
