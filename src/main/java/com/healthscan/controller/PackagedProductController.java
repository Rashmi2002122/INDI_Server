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

    @GetMapping("/products/barcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable("barcode") String barcode) {
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
}
