package com.togomovie.togomovie_ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
}
