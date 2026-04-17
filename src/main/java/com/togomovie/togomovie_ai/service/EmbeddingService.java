package com.togomovie.togomovie_ai.service;

public interface EmbeddingService {

    /** Index all movies from togomovie-backend into the vector store. */
    void indexAllMovies();

    /** Index or re-index a single movie by ID. */
    void indexMovie(Long movieId);

    /** Remove a movie from the vector store. */
    void removeMovie(Long movieId);
}
