package com.jeet.photo_store.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PhotoPageResponseDto {
    private List<PhotoResponseDto> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
}
