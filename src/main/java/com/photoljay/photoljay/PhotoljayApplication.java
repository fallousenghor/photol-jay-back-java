package com.photoljay.photoljay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // ← Ajoutez cette ligne
public class PhotoljayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoljayApplication.class, args);
	}

}
