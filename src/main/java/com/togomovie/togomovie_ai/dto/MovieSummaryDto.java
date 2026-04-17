package com.togomovie.togomovie_ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSummaryDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate releaseDate;
    private Double averageRating;
    private String language;
    private String country;
    private String posterUrl;
    private List<String> genres;
}
