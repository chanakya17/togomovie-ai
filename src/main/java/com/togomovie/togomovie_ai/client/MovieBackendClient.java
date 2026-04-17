package com.togomovie.togomovie_ai.client;

import com.togomovie.togomovie_ai.dto.MovieSummaryDto;
import com.togomovie.togomovie_ai.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * HTTP client for togomovie-backend REST API.
 * All calls are read-only (GET). The AI service never mutates backend data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovieBackendClient {

    private final RestClient backendRestClient;

    /**
     * Fetch a page of movies, optionally filtered.
     */
    public PagedResponse<MovieSummaryDto> getMovies(int page, int size, String title,
                                                     String genre, Integer year) {
        String uri = UriComponentsBuilder.fromPath("/movies")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParamIfPresent("title", java.util.Optional.ofNullable(title))
                .queryParamIfPresent("genre", java.util.Optional.ofNullable(genre))
                .queryParamIfPresent("year",  java.util.Optional.ofNullable(year))
                .toUriString();

        log.debug("Fetching movies from backend: {}", uri);

        return backendRestClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Fetch all movies for embedding indexing (iterates pages).
     */
    public List<MovieSummaryDto> getAllMoviesForIndexing() {
        int page = 0;
        final int pageSize = 100;
        List<MovieSummaryDto> all = new java.util.ArrayList<>();

        while (true) {
            PagedResponse<MovieSummaryDto> response = getMovies(page, pageSize, null, null, null);
            if (response == null || response.getContent() == null || response.getContent().isEmpty()) break;
            all.addAll(response.getContent());
            if (page >= response.getTotalPages() - 1) break;
            page++;
        }

        log.info("Fetched {} movies from backend for indexing", all.size());
        return all;
    }

    /**
     * Fetch a single movie by ID.
     */
    public MovieSummaryDto getMovieById(Long id) {
        return backendRestClient.get()
                .uri("/movies/{id}", id)
                .retrieve()
                .body(MovieSummaryDto.class);
    }
}
