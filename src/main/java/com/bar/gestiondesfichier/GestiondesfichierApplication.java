package com.bar.gestiondesfichier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestiondesfichierApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestiondesfichierApplication.class, args);
	}

}