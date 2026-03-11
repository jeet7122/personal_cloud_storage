package com.jeet.photo_store.services.command;

import com.jeet.photo_store.dtos.PhotoResponseDto;
import com.jeet.photo_store.dtos.events.PhotoUploadEvent;
import com.jeet.photo_store.models.Photo;
import com.jeet.photo_store.models.PhotoStatus;
import com.jeet.photo_store.repository.PhotoRepository;
import com.jeet.photo_store.services.validation.FileValidationService;
import com.jeet.photo_store.services.producer.PhotoUploadProducer;
import com.jeet.photo_store.services.storage.StorageService;
import com.jeet.photo_store.services.query.PhotoQueryService;
import com.jeet.photo_store.utils.AppMappers;
import com.jeet.photo_store.utils.FileHashUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoCommandServiceImpl implements PhotoCommandService {

    private final FileHashUtils fileHashUtils;
    private final FileValidationService fileValidationService;
    private final PhotoRepository photoRepository;
    private final AppMappers appMappers;
    private final PhotoUploadProducer kafkaUploadProducer;
    private static final Logger log = LoggerFactory.getLogger(PhotoCommandServiceImpl.class);
    private final StorageService storageService;
    private final PhotoQueryService photoQueryService;

    @Override
    public PhotoResponseDto uploadPhoto(MultipartFile file) {

        fileValidationService.validateImage(file);

        String hash = fileHashUtils.sha256(file);

        Photo existingPhoto = photoRepository.findByFileHash(hash).orElse(null);
        if (existingPhoto != null) {
            return appMappers.mapToPhotoResponse(existingPhoto, true);
        }

        UUID photoId = UUID.randomUUID();
        Path tempFile = null;

        try {
            String extension = getFileExtension(file.getOriginalFilename());

            Path tempDir = Paths.get("/tmp/cloudvault");
            Files.createDirectories(tempDir);

            tempFile = Files.createTempFile(tempDir, "upload-", extension);
            file.transferTo(tempFile.toFile());

            log.info("Temp file created at {}", tempFile);

            PhotoUploadEvent event = PhotoUploadEvent.builder()
                    .photoId(photoId)
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileHash(hash)
                    .size(file.getSize())
                    .tempFilePath(tempFile.toString())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            kafkaUploadProducer.publishUploadEvent(event);

            log.info("Upload event published: {}", photoId);

            return PhotoResponseDto.builder()
                    .id(photoId)
                    .originalFileName(event.getOriginalFileName())
                    .size(event.getSize())
                    .contentType(event.getContentType())
                    .status("PROCESSING")
                    .duplicate(false)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process upload for photoId={} tempFile={}",
                    photoId,
                    tempFile,
                    e);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to process file upload",
                    e
            );
        }
    }

    @Override
    public List<PhotoResponseDto> uploadPhotos(MultipartFile[] files) {
        if (files == null || files.length == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one file is required!");
        }
        return Arrays.stream(files)
                .map(this::uploadPhoto)
                .toList();
    }

    @Override
    public void deletePhoto(UUID id) {
        Photo photo = photoQueryService.getPhotoById(id);
        storageService.deleteFile(photo.getObjectKey());
        photoRepository.delete(photo);
    }


    private String getFileExtension(String filename) {
        if (filename == null) return ".tmp";
        int i = filename.lastIndexOf(".");
        return i > 0 ? filename.substring(i) : ".tmp";
    }
}
