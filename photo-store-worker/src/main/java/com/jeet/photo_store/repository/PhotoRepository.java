package com.jeet.photo_store.repository;

import com.jeet.photo_store.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    Optional<Photo> findByFileHash(String fileHash);
}
