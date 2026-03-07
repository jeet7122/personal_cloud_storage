package com.jeet.photo_store.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PhotoResponseDto {
    private UUID id;
    private String originalFileName;
    private String contentType;
    private Long size;
    private LocalDateTime uploadedAt;
    private boolean duplicate;
}
