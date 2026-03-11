package com.jeet.photo_store.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService{
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Override
    public String uploadFile(File file, String contentType) {
        String objectKey = generateObjectKey(file.getName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
        return objectKey;
    }

    private String generateObjectKey(String originalFileName) {
        String safeFileName = originalFileName == null ? "file" : originalFileName.replaceAll("\\s+", "_");
        return "photos/" + UUID.randomUUID() + "-" + safeFileName;
    }
}
