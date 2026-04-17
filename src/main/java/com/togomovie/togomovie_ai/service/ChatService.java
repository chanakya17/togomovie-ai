package com.togomovie.togomovie_ai.service;

import reactor.core.publisher.Flux;

public interface ChatService {

    /**
     * Process a chat message and return a streaming SSE response.
     *
     * @param message        The user's message
     * @param conversationId Optional conversation ID for multi-turn context
     * @return Flux of string chunks (SSE tokens)
     */
    Flux<String> chat(String message, String conversationId);
}
