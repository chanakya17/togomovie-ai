package com.togomovie.togomovie_ai.service.impl;

import com.togomovie.togomovie_ai.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider movieTools;

    /** Single shared memory store — handles per-conversation history internally */
    private final MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    public ChatServiceImpl(ChatClient chatClient, ToolCallbackProvider movieTools) {
        this.chatClient  = chatClient;
        this.movieTools  = movieTools;
    }

    @Override
    public Flux<String> chat(String message, String conversationId) {
        String convId = conversationId != null ? conversationId : "default";
        log.debug("Chat request convId={} message={}", convId, message);

        return chatClient.prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(convId).build())
                .tools(movieTools)
                .stream()
                .content();
    }
}
