package com.fnkcode.controller;

import com.fnkcode.exceptions.NotFoundException;
import com.fnkcode.sla.StrictSLA;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.InterruptedIOException;

import static com.fnkcode.sla.SLAValues.shortRetryMaxTimeBudgetMillis;

@RestController
@RequestMapping("/api/rs")
public class AppResillineceController {

    private static final Logger log = LoggerFactory.getLogger(AppResillineceController.class);

    private final RestClient restClientShortTimeout;
    private final RestClient restClient;

    public AppResillineceController(RestClient restClientShortTimeout, RestClient restClient) {
        this.restClientShortTimeout = restClientShortTimeout;
        this.restClient = restClient;
    }

    @GetMapping("/ok")
    public ResponseEntity<String> getOkResponse() {
        return restClientShortTimeout.get()
                .uri("http://localhost:8080/api/ok")
                .retrieve()
                .toEntity(String.class);
    }

    @Retry(name = "shortRetry", fallbackMethod = "getNotOkResponseFallback")
    @GetMapping("/nok")
    @StrictSLA(value = shortRetryMaxTimeBudgetMillis)
    @CircuitBreaker(name = "normalCB")
    public ResponseEntity<String> getNotOkResponse() {
        log.info("calling nok");
        return restClientShortTimeout.get()
                .uri("http://localhost:8080/api/nok")
                .retrieve()
                .toEntity(String.class);
    }
    private ResponseEntity<String> getNotOkResponseFallback(Exception e) {
        log.info(e.getMessage());
        throw new RuntimeException(e.getMessage());
    }
    @GetMapping("/nok2")
    @CircuitBreaker(name = "fnkCB", fallbackMethod = "fnkFallback")
    public ResponseEntity<String> getNotOk2Response() throws InterruptedIOException {
        throw new NotFoundException("test", "COMMUNICATION");
    }

    public ResponseEntity<String> fnkFallback(Throwable t) {
        if (t instanceof CallNotPermittedException) {
            log.info("Circuit is OPEN! Fast-failing now.");
            return ResponseEntity.status(503).body("Circuit Open: try again later");
        }

        log.error("Method failed, but circuit is still closed/closing: {}", t.getMessage());
        return ResponseEntity.status(404).body("Not Found: " + t.getMessage());
    }


}
