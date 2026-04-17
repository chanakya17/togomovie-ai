package com.togomovie.togomovie_ai.controller;

import com.togomovie.togomovie_ai.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/embeddings")
@RequiredArgsConstructor
@Tag(name = "Embedding Admin", description = "Manage the vector store index")
public class EmbeddingAdminController {

    private final EmbeddingService embeddingService;

    @PostMapping("/reindex")
    @Operation(summary = "Re-index all movies from togomovie-backend (async)")
    public ResponseEntity<String> reindexAll() {
        embeddingService.indexAllMovies();
        return ResponseEntity.accepted().body("Full re-indexing started asynchronously");
    }

    @PostMapping("/{movieId}")
    @Operation(summary = "Index or re-index a single movie")
    public ResponseEntity<String> indexMovie(@PathVariable Long movieId) {
        embeddingService.indexMovie(movieId);
        return ResponseEntity.ok("Movie " + movieId + " indexed");
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Remove a movie from the vector store")
    public ResponseEntity<String> removeMovie(@PathVariable Long movieId) {
        embeddingService.removeMovie(movieId);
        return ResponseEntity.ok("Movie " + movieId + " removed from index");
    }
}
