package com.togomovie.togomovie_ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    @Value("${internal.api-key}")
    private String internalApiKey;

    /**
     * RestClient pre-configured to call togomovie-backend.
     * Passes the internal API key so backend can identify AI-service requests.
     */
    @Bean
    public RestClient backendRestClient() {
        return RestClient.builder()
                .baseUrl(backendBaseUrl)
                .defaultHeader("X-Internal-Api-Key", internalApiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
