package com.jeet.photo_store.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoUrlResponseDto {
    private String url;
}
