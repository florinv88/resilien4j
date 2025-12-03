package com.fnkcode.controller;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/rs")
public class AppResillineceController {

    private static final Logger log = LoggerFactory.getLogger(AppResillineceController.class);
    private final RestClient restClient = RestClient.create();

    @GetMapping("/ok")
    public ResponseEntity<String> getOkResponse() {
        return restClient.get()
                .uri("http://localhost:8080/api/ok")
                .retrieve()
                .toEntity(String.class);
    }

    @Retry(name = "basicRetry", fallbackMethod = "getNotOkResponseFallback")
    @GetMapping("/nok")
    public ResponseEntity<String> getNotOkResponse() {
        log.info("calling nok");
        return restClient.get()
                .uri("http://localhost:8080/api/nok")
                .retrieve()
                .toEntity(String.class);
    }
    private ResponseEntity<String> getNotOkResponseFallback(Exception e) {
        log.info(e.getMessage());
        throw new RuntimeException(e.getMessage());
    }
}
