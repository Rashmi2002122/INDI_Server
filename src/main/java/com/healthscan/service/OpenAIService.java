package com.healthscan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openAiUrl;

    @Value("${openai.api.key:mock-key-for-dev}")
    private String apiKey;

    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.contains("mock-key");
    }
}
