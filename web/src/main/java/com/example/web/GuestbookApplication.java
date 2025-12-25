package com.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example.persistence.repository")
@EntityScan(basePackages = "com.example.persistence.entity")
public class GuestbookApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuestbookApplication.class, args);
    }
}