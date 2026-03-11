package com.jeet.photo_store.services;

import java.io.File;

public interface StorageService {
    String uploadFile(File file, String contentType);
}
