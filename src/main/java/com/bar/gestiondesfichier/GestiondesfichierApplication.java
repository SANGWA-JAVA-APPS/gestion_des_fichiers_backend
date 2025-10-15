package com.bar.gestiondesfichier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GestiondesfichierApplication {

    public static void main(String[] args) {
        System.out.println("Testing the workflow");
        SpringApplication.run(GestiondesfichierApplication.class, args);
    }

}
