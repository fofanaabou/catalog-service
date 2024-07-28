package com.sinignaci.catalogservice.controller;

import com.sinignaci.catalogservice.config.PolarProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final PolarProperties polarProperties;
    @GetMapping
    public ResponseEntity<String> getGreeting() {
        return ResponseEntity.ok(polarProperties.greeting());
    }
}
