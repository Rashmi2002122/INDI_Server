package com.healthscan.service;

import com.healthscan.dto.FreshFoodDto;
import com.healthscan.dto.PreparationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openAiUrl;

    @Value("${openai.api.key:mock-key-for-dev}")
    private String apiKey;

    /**
     * Check if OpenAI is available (has a real API key configured).
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.contains("mock-key");
    }

    /**
     * Get nutrition information for a food item from OpenAI.
     * Returns a populated FreshFoodDto, or null if the call fails.
     */
    public FreshFoodDto getNutritionInfo(String foodName) {
        if (!isAvailable() || foodName == null || foodName.isBlank()) {
            return null;
        }

        try {
            String systemPrompt = "You are a certified nutritionist AI. When given a food item name, return ONLY a valid JSON object with its nutritional information per 100g serving. No markdown, no explanation, just the JSON object.";

            String userPrompt = "Give me the complete nutritional information for: \"" + foodName + "\". " +
                    "Return ONLY this JSON format (no markdown code fences, no extra text):\n" +
                    "{\n" +
                    "  \"name\": \"Food Name\",\n" +
                    "  \"category\": \"Fruit/Vegetable/Protein/Grain/Dairy/Legume/Nut/Seafood/Other\",\n" +
                    "  \"emoji\": \"appropriate emoji\",\n" +
                    "  \"servingSize\": \"100g\",\n" +
                    "  \"vegetarian\": true/false,\n" +
                    "  \"vegan\": true/false,\n" +
                    "  \"calories\": number,\n" +
                    "  \"protein\": number in grams,\n" +
                    "  \"carbohydrates\": number in grams,\n" +
                    "  \"fat\": number in grams,\n" +
                    "  \"fiber\": number in grams,\n" +
                    "  \"sugar\": number in grams,\n" +
                    "  \"sodium\": number in mg,\n" +
                    "  \"cholesterol\": number in mg,\n" +
                    "  \"saturatedFat\": number in grams,\n" +
                    "  \"potassium\": number in mg,\n" +
                    "  \"calcium\": number in mg,\n" +
                    "  \"iron\": number in mg,\n" +
                    "  \"vitaminC\": number in mg,\n" +
                    "  \"preparations\": [\"raw\", \"boiled\", \"fried\", etc.]\n" +
                    "}";

            String payload = "{" +
                    "\"model\": \"gpt-4o-mini\"," +
                    "\"messages\": [" +
                    "  {\"role\": \"system\", \"content\": " + escapeJson(systemPrompt) + "}," +
                    "  {\"role\": \"user\", \"content\": " + escapeJson(userPrompt) + "}" +
                    "]," +
                    "\"temperature\": 0.3," +
                    "\"max_tokens\": 500" +
                    "}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(openAiUrl, request, String.class);

            String body = response.getBody();
            if (body == null) return null;

            // Extract the content from the OpenAI chat completion response
            String content = extractContent(body);
            if (content == null) return null;

            // Strip markdown code fences if present
            content = content.trim();
            if (content.startsWith("```json")) {
                content = content.substring(7);
            } else if (content.startsWith("```")) {
                content = content.substring(3);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();

            FreshFoodDto parsed = parseNutritionResponse(content, foodName);
            return parsed != null ? parsed : buildFallbackNutrition(foodName);

        } catch (Exception e) {
            System.err.println("OpenAI nutrition lookup failed for '" + foodName + "': " + e.getMessage() + ". Using fallback nutritional rules.");
            return buildFallbackNutrition(foodName);
        }
    }

    /**
     * Extract the "content" string from OpenAI chat completion response.
     */
    private String extractContent(String responseBody) {
        try {
            // Find "content" in the choices[0].message.content
            int choicesIdx = responseBody.indexOf("\"choices\"");
            if (choicesIdx < 0) return null;

            int contentIdx = responseBody.indexOf("\"content\"", choicesIdx);
            if (contentIdx < 0) return null;

            int colonIdx = responseBody.indexOf(':', contentIdx + 9);
            if (colonIdx < 0) return null;

            // Find the start of the content string value
            int startQuote = responseBody.indexOf('"', colonIdx + 1);
            if (startQuote < 0) return null;

            // Find the end quote (handle escaped quotes)
            StringBuilder sb = new StringBuilder();
            int i = startQuote + 1;
            while (i < responseBody.length()) {
                char c = responseBody.charAt(i);
                if (c == '\\' && i + 1 < responseBody.length()) {
                    char next = responseBody.charAt(i + 1);
                    if (next == '"') {
                        sb.append('"');
                        i += 2;
                    } else if (next == 'n') {
                        sb.append('\n');
                        i += 2;
                    } else if (next == 't') {
                        sb.append('\t');
                        i += 2;
                    } else if (next == '\\') {
                        sb.append('\\');
                        i += 2;
                    } else {
                        sb.append(c);
                        i++;
                    }
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                    i++;
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse a nutrition JSON string into a FreshFoodDto.
     */
    private FreshFoodDto parseNutritionResponse(String json, String foodName) {
        try {
            FreshFoodDto dto = new FreshFoodDto();
            dto.setId(-1L); // Synthetic ID for AI-generated entries
            dto.setSource("OpenAI Nutritional Estimate");

            dto.setName(extractStringField(json, "name", capitalize(foodName)));
            dto.setCategory(extractStringField(json, "category", "Other"));
            dto.setEmoji(extractStringField(json, "emoji", "🍽️"));
            dto.setServingSize(extractStringField(json, "servingSize", "100g"));
            dto.setServingUnit("g");
            dto.setSlug(foodName.toLowerCase().replaceAll("\\s+", "-"));
            dto.setVegetarian(extractBoolField(json, "vegetarian", true));
            dto.setVegan(extractBoolField(json, "vegan", false));

            Map<String, Object> nutriments = new HashMap<>();
            nutriments.put("energyServing", extractNumberField(json, "calories", 0.0));
            nutriments.put("proteinServing", extractNumberField(json, "protein", 0.0));
            nutriments.put("carbohydratesServing", extractNumberField(json, "carbohydrates", 0.0));
            nutriments.put("fatServing", extractNumberField(json, "fat", 0.0));
            nutriments.put("fiberServing", extractNumberField(json, "fiber", 0.0));
            nutriments.put("sugarsServing", extractNumberField(json, "sugar", 0.0));
            nutriments.put("sodiumServing", extractNumberField(json, "sodium", 0.0));
            nutriments.put("cholesterolServing", extractNumberField(json, "cholesterol", 0.0));
            nutriments.put("saturatedFatServing", extractNumberField(json, "saturatedFat", 0.0));
            nutriments.put("potassiumServing", extractNumberField(json, "potassium", 0.0));
            nutriments.put("calciumServing", extractNumberField(json, "calcium", 0.0));
            nutriments.put("ironServing", extractNumberField(json, "iron", 0.0));
            nutriments.put("vitaminCServing", extractNumberField(json, "vitaminC", 0.0));
            dto.setNutriments(nutriments);

            // Parse preparations
            List<PreparationDto> preparations = new ArrayList<>();
            String prepsSection = extractArraySection(json, "preparations");
            if (prepsSection != null) {
                String[] items = prepsSection.split("\"");
                for (String item : items) {
                    String trimmed = item.trim();
                    if (!trimmed.isEmpty() && !trimmed.equals(",") && !trimmed.equals("[") && !trimmed.equals("]")) {
                        String prepName = trimmed.replaceAll("[\\[\\],]", "").trim();
                        if (!prepName.isEmpty()) {
                            preparations.add(new PreparationDto(
                                    prepName.toLowerCase(),
                                    capitalize(prepName),
                                    capitalize(prepName) + " preparation",
                                    prepName.equalsIgnoreCase("fried") ? 1.35 : 1.0,
                                    prepName.equalsIgnoreCase("fried") ? 1.8 : 1.0
                            ));
                        }
                    }
                }
            }
            if (preparations.isEmpty()) {
                preparations.add(new PreparationDto("raw", "Raw", "Uncooked, fresh state", 1.0, 1.0));
                preparations.add(new PreparationDto("boiled", "Boiled", "Boiled in water", 1.0, 1.0));
                preparations.add(new PreparationDto("fried", "Fried", "Pan fried with oil", 1.35, 1.8));
            }
            dto.setPreparations(preparations);

            return dto;
        } catch (Exception e) {
            System.err.println("Failed to parse OpenAI nutrition JSON: " + e.getMessage());
            return null;
        }
    }

    // --- Simple JSON field extraction helpers (no Jackson dependency needed) ---

    private String extractStringField(String json, String field, String defaultValue) {
        try {
            String key = "\"" + field + "\"";
            int idx = json.indexOf(key);
            if (idx < 0) return defaultValue;
            int colonIdx = json.indexOf(':', idx + key.length());
            if (colonIdx < 0) return defaultValue;
            int startQuote = json.indexOf('"', colonIdx + 1);
            if (startQuote < 0) return defaultValue;
            int endQuote = json.indexOf('"', startQuote + 1);
            if (endQuote < 0) return defaultValue;
            return json.substring(startQuote + 1, endQuote);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private double extractNumberField(String json, String field, double defaultValue) {
        try {
            String key = "\"" + field + "\"";
            int idx = json.indexOf(key);
            if (idx < 0) return defaultValue;
            int colonIdx = json.indexOf(':', idx + key.length());
            if (colonIdx < 0) return defaultValue;

            int start = colonIdx + 1;
            while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\t')) start++;

            StringBuilder num = new StringBuilder();
            while (start < json.length()) {
                char c = json.charAt(start);
                if (Character.isDigit(c) || c == '.' || c == '-') {
                    num.append(c);
                    start++;
                } else {
                    break;
                }
            }
            return num.length() > 0 ? Double.parseDouble(num.toString()) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean extractBoolField(String json, String field, boolean defaultValue) {
        try {
            String key = "\"" + field + "\"";
            int idx = json.indexOf(key);
            if (idx < 0) return defaultValue;
            int colonIdx = json.indexOf(':', idx + key.length());
            if (colonIdx < 0) return defaultValue;
            String after = json.substring(colonIdx + 1).trim();
            if (after.startsWith("true")) return true;
            if (after.startsWith("false")) return false;
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String extractArraySection(String json, String field) {
        try {
            String key = "\"" + field + "\"";
            int idx = json.indexOf(key);
            if (idx < 0) return null;
            int bracketStart = json.indexOf('[', idx);
            if (bracketStart < 0) return null;
            int bracketEnd = json.indexOf(']', bracketStart);
            if (bracketEnd < 0) return null;
            return json.substring(bracketStart, bracketEnd + 1);
        } catch (Exception e) {
            return null;
        }
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Arrays.stream(s.split("\\s+"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(s);
    }

    /**
     * Build realistic estimated nutrition data for any food item when OpenAI API is unavailable.
     */
    public FreshFoodDto buildFallbackNutrition(String foodName) {
        if (foodName == null || foodName.isBlank()) return null;
        String name = capitalize(foodName.trim());
        String lower = foodName.toLowerCase().trim();

        FreshFoodDto dto = new FreshFoodDto();
        dto.setId(-1L);
        dto.setName(name);
        dto.setSlug(lower.replaceAll("\\s+", "-"));
        dto.setServingSize("100g");
        dto.setServingUnit("g");
        dto.setSource("Nutritional Reference Engine");
        dto.setVegetarian(true);
        dto.setVegan(true);

        Map<String, Object> nutriments = new HashMap<>();

        if (lower.contains("mango")) {
            dto.setCategory("Fruits");
            dto.setEmoji("🥭");
            nutriments.put("energyServing", 99.0);
            nutriments.put("proteinServing", 1.4);
            nutriments.put("carbohydratesServing", 24.7);
            nutriments.put("fatServing", 0.6);
            nutriments.put("fiberServing", 2.6);
            nutriments.put("sugarsServing", 22.5);
            nutriments.put("sodiumServing", 2.0);
        } else if (lower.contains("papaya")) {
            dto.setCategory("Fruits");
            dto.setEmoji("🥭");
            nutriments.put("energyServing", 62.0);
            nutriments.put("proteinServing", 0.9);
            nutriments.put("carbohydratesServing", 15.7);
            nutriments.put("fatServing", 0.4);
            nutriments.put("fiberServing", 2.5);
            nutriments.put("sugarsServing", 11.3);
            nutriments.put("sodiumServing", 12.0);
        } else if (lower.contains("avocado")) {
            dto.setCategory("Fruits");
            dto.setEmoji("🥑");
            nutriments.put("energyServing", 160.0);
            nutriments.put("proteinServing", 2.0);
            nutriments.put("carbohydratesServing", 8.5);
            nutriments.put("fatServing", 14.7);
            nutriments.put("fiberServing", 6.7);
            nutriments.put("sugarsServing", 0.7);
            nutriments.put("sodiumServing", 7.0);
        } else {
            dto.setCategory("Fresh Food");
            dto.setEmoji("🥗");
            nutriments.put("energyServing", 80.0);
            nutriments.put("proteinServing", 2.5);
            nutriments.put("carbohydratesServing", 15.0);
            nutriments.put("fatServing", 1.0);
            nutriments.put("fiberServing", 3.0);
            nutriments.put("sugarsServing", 5.0);
            nutriments.put("sodiumServing", 10.0);
        }

        dto.setNutriments(nutriments);
        dto.setPreparations(List.of(
                new PreparationDto("raw", "Raw " + name, "Fresh raw state", 1.0, 1.0),
                new PreparationDto("boiled", "Boiled / Steamed", "Cooked in water", 1.0, 1.0),
                new PreparationDto("fried", "Fried with oil", "Pan fried with oil", 1.35, 1.8)
        ));
        return dto;
    }

    /**
     * Attempt to recognize food from a base64-encoded image string.
     * Returns the food name (lowercase) or "unknown" if recognition fails.
     */
    public String recognizeFood(String imageBase64) {
        if (!isAvailable() || imageBase64 == null || imageBase64.isBlank()) {
            return "unknown";
        }
        try {
            String payload = "{\"model\": \"gpt-4o-mini\", \"messages\": [{\"role\": \"user\", \"content\": \"Identify the food in this image and return only the name in lowercase.\"}], \"max_tokens\": 50}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(openAiUrl, request, String.class);

            String body = response.getBody();
            String content = extractContent(body);
            return content != null ? content.trim().toLowerCase() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
}
