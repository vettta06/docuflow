package com.accounting.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/accountant")
public class AccountantController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "message", "Hello, ACCOUNTANT",
                "email", auth.getName()
        ));
    }
}
