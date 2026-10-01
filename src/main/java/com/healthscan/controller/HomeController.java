package com.healthscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
            "service", "INDI Integrative Health Backend API",
            "status", "UP",
            "health", "/api/health",
            "documentation", "/swagger-ui.html"
        ));
    }
}
