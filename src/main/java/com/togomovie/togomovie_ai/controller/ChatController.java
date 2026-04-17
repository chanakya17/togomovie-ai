package com.togomovie.togomovie_ai.controller;

import com.togomovie.togomovie_ai.dto.ChatRequest;
import com.togomovie.togomovie_ai.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "AI chat endpoint with RAG and tool use")
public class ChatController {

    private final ChatService chatService;

    /**
     * POST /api/chat
     * Accepts a user message and streams back the AI response as SSE tokens.
     *
     * Example curl:
     *   curl -N -X POST http://localhost:8081/api/chat \
     *     -H "Content-Type: application/json" \
     *     -d '{"message":"Recommend me a good action movie"}'
     */
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Chat with the movie AI assistant (streaming SSE)")
    public Flux<String> chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request.getMessage(), request.getConversationId());
    }
}
