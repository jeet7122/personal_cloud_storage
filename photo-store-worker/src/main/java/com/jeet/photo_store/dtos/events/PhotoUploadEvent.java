package com.jeet.photo_store.dtos.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PhotoUploadEvent {
    private UUID photoId;
    private String originalFileName;
    private String contentType;
    private Long size;
    private String fileHash;
    private String tempFilePath;
    private LocalDateTime uploadedAt;
}
