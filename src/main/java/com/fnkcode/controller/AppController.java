package com.fnkcode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppController {

    @GetMapping("/ok")
    public ResponseEntity<String> getOkResponse() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/nok")
    public ResponseEntity<String> getNotOkResponse() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        return ResponseEntity.notFound().build();
    }
}
