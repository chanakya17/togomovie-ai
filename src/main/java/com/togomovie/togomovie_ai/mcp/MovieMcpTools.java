package com.togomovie.togomovie_ai.mcp;

import com.togomovie.togomovie_ai.client.MovieBackendClient;
import com.togomovie.togomovie_ai.dto.MovieSummaryDto;
import com.togomovie.togomovie_ai.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP tools exposed to AI agents and the ChatClient.
 * Each @Tool method becomes a callable function.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovieMcpTools {

    private final MovieBackendClient backendClient;

    @Tool(description = """
            Search for movies by title keyword, genre, or release year.
            Returns a list of matching movies with id, title, genres, rating, and release date.
            Use this when the user asks to find, search, or browse movies.
            """)
    public List<MovieSummaryDto> searchMovies(
            @ToolParam(description = "Part of the movie title to search for, or null") String title,
            @ToolParam(description = "Genre name to filter by (e.g. Action, Drama), or null") String genre,
            @ToolParam(description = "Release year as integer (e.g. 2023), or null") Integer year
    ) {
        log.info("MCP tool: searchMovies title={} genre={} year={}", title, genre, year);
        PagedResponse<MovieSummaryDto> response = backendClient.getMovies(0, 10, title, genre, year);
        return response != null ? response.getContent() : List.of();
    }

    @Tool(description = """
            Get the top-rated movies on the platform.
            Returns up to 10 movies sorted by average rating, highest first.
            Use this when the user asks for 'best movies', 'top rated', or 'most popular'.
            """)
    public List<MovieSummaryDto> getTopRatedMovies() {
        log.info("MCP tool: getTopRatedMovies");
        PagedResponse<MovieSummaryDto> response = backendClient.getMovies(0, 10, null, null, null);
        if (response == null || response.getContent() == null) return List.of();
        return response.getContent().stream()
                .sorted((a, b) -> Double.compare(
                        b.getAverageRating() != null ? b.getAverageRating() : 0,
                        a.getAverageRating() != null ? a.getAverageRating() : 0))
                .collect(Collectors.toList());
    }

    @Tool(description = """
            Get details of a specific movie by its numeric ID.
            Returns full movie info including description, genres, rating, language, and country.
            Use this when the user asks about a specific movie by name after you've found its ID.
            """)
    public MovieSummaryDto getMovieDetails(
            @ToolParam(description = "The numeric movie ID from a previous search") Long movieId
    ) {
        log.info("MCP tool: getMovieDetails id={}", movieId);
        return backendClient.getMovieById(movieId);
    }

    @Tool(description = """
            Get movies released in a specific year.
            Returns up to 10 movies from that year sorted by rating.
            """)
    public List<MovieSummaryDto> getMoviesByYear(
            @ToolParam(description = "The release year, e.g. 2024") Integer year
    ) {
        log.info("MCP tool: getMoviesByYear year={}", year);
        PagedResponse<MovieSummaryDto> response = backendClient.getMovies(0, 10, null, null, year);
        return response != null ? response.getContent() : List.of();
    }

    @Tool(description = """
            Get movies by a specific genre.
            Returns up to 10 movies from that genre.
            Common genres: Action, Drama, Comedy, Horror, Sci-Fi, Romance, Thriller, Animation.
            """)
    public List<MovieSummaryDto> getMoviesByGenre(
            @ToolParam(description = "The genre name, e.g. Action") String genre
    ) {
        log.info("MCP tool: getMoviesByGenre genre={}", genre);
        PagedResponse<MovieSummaryDto> response = backendClient.getMovies(0, 10, null, genre, null);
        return response != null ? response.getContent() : List.of();
    }
}
