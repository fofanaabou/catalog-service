package com.sinignaci.catalogservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class HomeController {
    @GetMapping
    public Mono<String> getGreeting() {
        return Mono.just("Hello world, welcome to the book catalog");
    }
}
