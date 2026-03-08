package com.jeet.photo_store.repository;

import com.jeet.photo_store.models.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    Optional<Photo> findByFileHash(String fileHash);

    Page<Photo> findAll(Pageable pageable);
}
