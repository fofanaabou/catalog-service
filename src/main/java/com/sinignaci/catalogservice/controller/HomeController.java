package com.sinignaci.catalogservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HomeController {
    @GetMapping
    public ResponseEntity<String> getGreeting() {
        return ResponseEntity.ok("Hello world, welcome to the book catalog");
    }
}
