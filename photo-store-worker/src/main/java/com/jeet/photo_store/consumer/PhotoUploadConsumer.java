package com.jeet.photo_store.consumer;

import com.jeet.photo_store.dtos.events.PhotoUploadEvent;
import com.jeet.photo_store.models.Photo;
import com.jeet.photo_store.models.PhotoStatus;
import com.jeet.photo_store.repository.PhotoRepository;
import com.jeet.photo_store.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class PhotoUploadConsumer {
    private static final Logger log = LoggerFactory.getLogger(PhotoUploadConsumer.class);
    private final PhotoRepository photoRepository;
    private final StorageService storageService;

    @KafkaListener(topics = "photo-upload", groupId = "photo-upload-workers")
    public void handleUpload(PhotoUploadEvent event) {

        log.info("Received upload event for photoId={} tempFile={}",
                event.getPhotoId(),
                event.getTempFilePath());

        try {

            File file = new File(event.getTempFilePath());

            if (!file.exists()) {
                throw new RuntimeException("Temp file missing: " + event.getTempFilePath());
            }

            log.info("Uploading file {} size={}", file.getAbsolutePath(), file.length());

            String objectKey = storageService.uploadFile(file, event.getContentType());

            Photo photo = Photo.builder()
                    .originalFileName(event.getOriginalFileName())
                    .objectKey(objectKey)
                    .contentType(event.getContentType())
                    .size(event.getSize())
                    .fileHash(event.getFileHash())
                    .status(PhotoStatus.COMPLETED)
                    .build();

            photoRepository.save(photo);

            Files.deleteIfExists(file.toPath());

            log.info("Upload completed for photoId={}", event.getPhotoId());

        } catch (Exception e) {

            log.error("Worker failed for photoId={} path={}",
                    event.getPhotoId(),
                    event.getTempFilePath(),
                    e);

            throw new RuntimeException(e);
        }
    }
}
