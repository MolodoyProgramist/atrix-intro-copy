package com.appsella.atrix.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @GetMapping("/api/test")
    public String test() {
        return "API is working!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
