package com.healthscan.controller;

import com.healthscan.service.OpenFoodFactsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PackagedProductController {

    private final OpenFoodFactsService openFoodFactsService;
    private List<String> userGoals = new ArrayList<>();

    public PackagedProductController(OpenFoodFactsService openFoodFactsService) {
        this.openFoodFactsService = openFoodFactsService;
    }

    private static final String BARCODE_PATTERN = "^[0-9A-Za-z_-]{1,64}$";
    private static final int MAX_RAW_JSON_LENGTH = 500_000; // 500 KB limit to prevent DoS

    @GetMapping("/products/barcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable("barcode") String barcode) {
        if (barcode == null || !barcode.matches(BARCODE_PATTERN)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid barcode format"));
        }

        String json = openFoodFactsService.fetchProductByBarcode(barcode);
        if (json == null || json.contains("\"status\":0")) {
            return ResponseEntity.status(404).body(
                    Map.of("error", "Product not found",
                           "barcode", barcode,
                           "message", "No product found for barcode: " + barcode + ". This product may not be in the OpenFoodFacts database.")
            );
        }
        return ResponseEntity.ok(json);
    }

    @PostMapping("/products/cache")
    public ResponseEntity<?> cacheProduct(@RequestBody Map<String, Object> payload) {
        if (payload != null && payload.containsKey("barcode") && payload.containsKey("rawJson")) {
            String barcode = String.valueOf(payload.get("barcode")).trim();
            String rawJson = String.valueOf(payload.get("rawJson")).trim();

            if (!barcode.matches(BARCODE_PATTERN)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid barcode format"));
            }

            if (rawJson.length() > MAX_RAW_JSON_LENGTH || !rawJson.startsWith("{")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON payload or payload too large"));
            }

            openFoodFactsService.saveRawJson(barcode, rawJson);
            return ResponseEntity.ok(Map.of("status", "cached", "barcode", barcode));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Missing barcode or rawJson"));
    }

    @GetMapping("/user/goals")
    public ResponseEntity<Map<String, Object>> getUserGoals() {
        return ResponseEntity.ok(Map.of("goals", userGoals));
    }

    @PutMapping("/user/goals")
    public ResponseEntity<Map<String, Object>> updateUserGoals(@RequestBody Map<String, Object> payload) {
        if (payload != null && payload.get("goals") instanceof List<?> gList) {
            this.userGoals = gList.stream().map(Object::toString).toList();
        } else {
            this.userGoals = new ArrayList<>();
        }
        return ResponseEntity.ok(Map.of("status", "success", "goals", this.userGoals));
    }

    @GetMapping("/products/{barcode}/alternatives")
    public ResponseEntity<Map<String, Object>> getProductAlternatives(@PathVariable("barcode") String barcode) {
        return ResponseEntity.ok(Map.of("alternatives", new ArrayList<>()));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "healthscan-backend"));
    }
}
