package com.jeet.photo_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class PhotoStoreWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoStoreWorkerApplication.class, args);
	}

}
