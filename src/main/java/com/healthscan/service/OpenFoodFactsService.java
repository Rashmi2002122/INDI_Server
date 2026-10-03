package com.healthscan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthscan.entity.PackagedProductEntity;
import com.healthscan.repository.PackagedProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Service that manages packaged food barcode lookups with DB caching and Live API fallback.
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
     * 1. Checks local MySQL DB first (<10ms instant response).
     * 2. If not cached, fetches from Open Food Facts API and caches in DB for future requests.
     */
    public String fetchProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }

        String cleanBarcode = barcode.trim();

        // Step 1: Check Local MySQL Database Cache
        try {
            Optional<PackagedProductEntity> cachedOpt = packagedProductRepository.findByBarcode(cleanBarcode);
            if (cachedOpt.isPresent() && cachedOpt.get().getRawJson() != null && !cachedOpt.get().getRawJson().isEmpty()) {
                System.out.println("⚡ [DB CACHE HIT] Served barcode " + cleanBarcode + " directly from local database.");
                return cachedOpt.get().getRawJson();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: DB cache lookup error for barcode " + cleanBarcode + ": " + e.getMessage());
        }

        // Step 2: Live API Request to Open Food Facts
        try {
            System.out.println("🌐 [LIVE API CALL] Fetching barcode " + cleanBarcode + " from Open Food Facts API...");
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .pathSegment(cleanBarcode)
                    .toUriString();

            String rawJson = restTemplate.getForObject(url, String.class);

            if (rawJson != null && rawJson.contains("\"status\":1")) {
                // Step 3: Cache new product into local MySQL database asynchronously/safely
                saveToDatabase(cleanBarcode, rawJson);
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
     * Parses product fields from Open Food Facts JSON and saves to MySQL database.
     */
    private void saveToDatabase(String barcode, String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode product = root.path("product");

            if (!product.isMissingNode()) {
                String productName = product.path("product_name").asText(null);
                String brand = product.path("brands").asText(null);
                String quantity = product.path("quantity").asText(null);
                String servingSize = product.path("serving_size").asText(null);
                String categories = product.path("categories").asText(null);
                String ingredientsText = product.path("ingredients_text").asText(null);
                String allergens = product.path("allergens").asText(null);

                PackagedProductEntity entity = new PackagedProductEntity();
                entity.setBarcode(barcode);
                entity.setProductName(productName);
                entity.setBrand(brand);
                entity.setQuantity(quantity);
                entity.setServingSize(servingSize);
                entity.setCategories(categories);
                entity.setIngredientsText(ingredientsText);
                entity.setAllergens(allergens);
                entity.setRawJson(rawJson);

                packagedProductRepository.save(entity);
                System.out.println("💾 [DB CACHED] Saved barcode " + barcode + " (" + productName + ") to local MySQL database.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to parse and save barcode " + barcode + " to DB: " + e.getMessage());
        }
    }
}
