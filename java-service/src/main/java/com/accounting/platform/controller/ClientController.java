package com.accounting.platform.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController {

  @GetMapping("/ping")
  public ResponseEntity<Map<String, Object>> ping(Authentication auth) {
    return ResponseEntity.ok(Map.of("message", "Hello, CLIENT", "email", auth.getName()));
  }
}
