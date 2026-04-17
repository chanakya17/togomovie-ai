package com.togomovie.togomovie_ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "Message must not be blank")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

    /** Optional conversation ID for multi-turn sessions */
    private String conversationId;
}
