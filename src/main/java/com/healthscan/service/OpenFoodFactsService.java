package com.healthscan.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Service that proxies calls to the Open Food Facts API for packaged food barcode lookup.
 */
@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v2/product";

    /**
     * Fetch product data from Open Food Facts by barcode.
     * Returns the raw JSON response string, or null if the product is not found
     * or an error occurs.
     */
    public String fetchProductByBarcode(String barcode) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .pathSegment(barcode)
                    .toUriString();
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            // Product not found on OpenFoodFacts — this is expected for unknown barcodes
            return null;
        } catch (HttpClientErrorException e) {
            // Other HTTP client errors (400, 403, etc.)
            return null;
        } catch (RestClientException e) {
            // Network errors, timeouts, etc.
            return null;
        }
    }
}

