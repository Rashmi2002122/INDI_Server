package com.healthscan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthscan.entity.PackagedProductEntity;
import com.healthscan.repository.PackagedProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Service that manages packaged food barcode lookups with Aiven MySQL DB caching and Live API fallback.
 */
@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final PackagedProductRepository packagedProductRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/product";

    public OpenFoodFactsService(PackagedProductRepository packagedProductRepository) {
        this.packagedProductRepository = packagedProductRepository;
    }

    /**
     * Lookup product by barcode:
     * 1. Checks Aiven Cloud MySQL DB first (<10ms instant response).
     * 2. If not cached, fetches from Open Food Facts API and saves to Aiven DB for future requests.
     */
    public String fetchProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }

        String cleanBarcode = barcode.trim();

        // Step 1: Check Aiven Cloud MySQL Database Cache
        try {
            Optional<PackagedProductEntity> cachedOpt = packagedProductRepository.findByBarcode(cleanBarcode);
            if (cachedOpt.isPresent() && cachedOpt.get().getRawJson() != null && !cachedOpt.get().getRawJson().isEmpty()) {
                System.out.println("⚡ [DB CACHE HIT] Served barcode " + cleanBarcode + " directly from Aiven MySQL database.");
                return cachedOpt.get().getRawJson();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Aiven DB cache lookup error for barcode " + cleanBarcode + ": " + e.getMessage());
        }

        // Step 2: Live API Request to Open Food Facts
        try {
            System.out.println("🌐 [LIVE API CALL] Fetching barcode " + cleanBarcode + " from Open Food Facts API...");
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .pathSegment(cleanBarcode)
                    .toUriString();

            String rawJson = restTemplate.getForObject(url, String.class);

            if (rawJson != null) {
                try {
                    JsonNode root = objectMapper.readTree(rawJson);
                    int status = root.path("status").asInt(-1);
                    if (status == 1 || (root.has("product") && !root.path("product").isMissingNode())) {
                        // Step 3: Cache new product into Aiven MySQL database
                        saveToDatabase(cleanBarcode, rawJson, root);
                    }
                } catch (Exception pErr) {
                    System.err.println("⚠️ JSON parse warning for barcode " + cleanBarcode + ": " + pErr.getMessage());
                }
            }

            return rawJson;
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("ℹ️ Product not found on Open Food Facts for barcode: " + cleanBarcode);
            return null;
        } catch (HttpClientErrorException e) {
            System.err.println("⚠️ Open Food Facts HTTP error (" + e.getStatusCode() + ") for barcode: " + cleanBarcode);
            return null;
        } catch (RestClientException e) {
            System.err.println("⚠️ Open Food Facts Network/Timeout error for barcode: " + cleanBarcode);
            return null;
        }
    }

    /**
     * Parses product fields from Open Food Facts JSON and saves to Aiven MySQL database.
     */
    @Transactional
    public void saveToDatabase(String barcode, String rawJson, JsonNode root) {
        try {
            JsonNode product = root.path("product");
            if (product.isMissingNode() || product.isNull()) {
                return;
            }

            String productName = product.path("product_name").asText(null);
            String brand = product.path("brands").asText(null);
            String quantity = product.path("quantity").asText(null);
            String servingSize = product.path("serving_size").asText(null);
            String categories = product.path("categories").asText(null);
            String ingredientsText = product.path("ingredients_text").asText(null);
            String allergens = product.path("allergens").asText(null);

            // Extract nutritional values per 100g
            JsonNode nutriments = product.path("nutriments");
            Double energyKcal = getDoubleVal(nutriments, "energy-kcal_100g", "energy-kcal");
            Double protein = getDoubleVal(nutriments, "proteins_100g", "proteins");
            Double carbs = getDoubleVal(nutriments, "carbohydrates_100g", "carbohydrates");
            Double sugar = getDoubleVal(nutriments, "sugars_100g", "sugars");
            Double fat = getDoubleVal(nutriments, "fat_100g", "fat");
            Double saturatedFat = getDoubleVal(nutriments, "saturated-fat_100g", "saturated-fat");
            Double transFat = getDoubleVal(nutriments, "trans-fat_100g", "trans-fat");
            Double fiber = getDoubleVal(nutriments, "fiber_100g", "fiber");
            Double sodium = getDoubleVal(nutriments, "sodium_100g", "sodium");

            PackagedProductEntity entity = packagedProductRepository.findByBarcode(barcode)
                    .orElseGet(() -> new PackagedProductEntity());

            entity.setBarcode(barcode);
            entity.setProductName(productName);
            entity.setBrand(brand);
            entity.setQuantity(quantity);
            entity.setServingSize(servingSize);
            entity.setCategories(categories);
            entity.setIngredientsText(ingredientsText);
            entity.setAllergens(allergens);
            entity.setEnergyKcal(energyKcal);
            entity.setProtein(protein);
            entity.setCarbohydrates(carbs);
            entity.setSugar(sugar);
            entity.setFat(fat);
            entity.setSaturatedFat(saturatedFat);
            entity.setTransFat(transFat);
            entity.setFiber(fiber);
            entity.setSodium(sodium);
            entity.setRawJson(rawJson);

            packagedProductRepository.saveAndFlush(entity);
            System.out.println("💾 [AIVEN DB SAVED] Barcode " + barcode + " (" + (productName != null ? productName : "Product") + ") successfully stored in Aiven MySQL database.");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to save barcode " + barcode + " to Aiven DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Double getDoubleVal(JsonNode node, String field1, String field2) {
        if (node == null || node.isMissingNode()) return null;
        if (node.has(field1) && !node.path(field1).isNull()) {
            return node.path(field1).asDouble();
        }
        if (node.has(field2) && !node.path(field2).isNull()) {
            return node.path(field2).asDouble();
        }
        return null;
    }
}
