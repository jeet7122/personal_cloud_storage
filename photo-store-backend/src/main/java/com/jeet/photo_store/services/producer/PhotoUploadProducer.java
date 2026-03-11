package com.jeet.photo_store.services.producer;

import com.jeet.photo_store.dtos.events.PhotoUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoUploadProducer {

    private final KafkaTemplate<String, PhotoUploadEvent> kafkaTemplate;

    public void publishUploadEvent(PhotoUploadEvent event){
        kafkaTemplate.send("photo-upload", event.getPhotoId().toString(), event);
    }
}
