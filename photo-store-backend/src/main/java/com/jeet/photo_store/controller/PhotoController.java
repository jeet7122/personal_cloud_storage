package com.jeet.photo_store.controller;

import com.jeet.photo_store.dtos.PhotoPageResponseDto;
import com.jeet.photo_store.dtos.PhotoResponseDto;
import com.jeet.photo_store.dtos.PhotoUrlResponseDto;
import com.jeet.photo_store.services.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping
    public ResponseEntity<PhotoPageResponseDto> getPhotos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(photoService.getPhotos(page, size));
    }

    @GetMapping("/{id}/url")
    public ResponseEntity<PhotoUrlResponseDto> getPhotoUrl(@PathVariable UUID id){
        return ResponseEntity.ok(photoService.getPhotoUrl(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable UUID id){
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PhotoResponseDto>> uploadPhotos(@RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.ok(photoService.uploadPhotos(files));
    }
}
