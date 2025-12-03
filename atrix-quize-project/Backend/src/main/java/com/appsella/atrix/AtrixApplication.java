package com.appsella.atrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AtrixApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtrixApplication.class, args);
    }
}
