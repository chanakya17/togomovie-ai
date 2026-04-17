package com.togomovie.togomovie_ai.service.impl;

import com.togomovie.togomovie_ai.client.MovieBackendClient;
import com.togomovie.togomovie_ai.dto.MovieSummaryDto;
import com.togomovie.togomovie_ai.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;
    private final MovieBackendClient backendClient;

    @Override
    @Async
    public void indexAllMovies() {
        log.info("Starting full movie indexing...");
        List<MovieSummaryDto> movies = backendClient.getAllMoviesForIndexing();

        List<Document> documents = movies.stream()
                .map(this::toDocument)
                .toList();

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
            log.info("Indexed {} movies into vector store", documents.size());
        } else {
            log.warn("No movies found to index");
        }
    }

    @Override
    public void indexMovie(Long movieId) {
        MovieSummaryDto movie = backendClient.getMovieById(movieId);
        if (movie == null) {
            log.warn("Movie {} not found, skipping indexing", movieId);
            return;
        }
        vectorStore.add(List.of(toDocument(movie)));
        log.info("Indexed movie id={} title={}", movieId, movie.getTitle());
    }

    @Override
    public void removeMovie(Long movieId) {
        vectorStore.delete(List.of(String.valueOf(movieId)));
        log.info("Removed movie id={} from vector store", movieId);
    }

    // -------------------------------------------------------------------------

    private Document toDocument(MovieSummaryDto movie) {
        String content = buildDocumentContent(movie);
        Map<String, Object> metadata = Map.of(
                "movieId",     movie.getId(),
                "title",       movie.getTitle() != null ? movie.getTitle() : "",
                "genres",      movie.getGenres() != null ? movie.getGenres() : List.of(),
                "rating",      movie.getAverageRating() != null ? movie.getAverageRating() : 0.0,
                "language",    movie.getLanguage() != null ? movie.getLanguage() : "",
                "releaseDate", movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : ""
        );
        return new Document(String.valueOf(movie.getId()), content, metadata);
    }

    private String buildDocumentContent(MovieSummaryDto movie) {
        return """
                Title: %s
                Genres: %s
                Release Year: %s
                Language: %s
                Country: %s
                Average Rating: %.1f/5
                Description: %s
                """.formatted(
                movie.getTitle(),
                movie.getGenres() != null ? String.join(", ", movie.getGenres()) : "Unknown",
                movie.getReleaseDate() != null ? movie.getReleaseDate().getYear() : "Unknown",
                movie.getLanguage() != null ? movie.getLanguage() : "Unknown",
                movie.getCountry() != null ? movie.getCountry() : "Unknown",
                movie.getAverageRating() != null ? movie.getAverageRating() : 0.0,
                movie.getDescription() != null ? movie.getDescription() : "No description available."
        );
    }
}
