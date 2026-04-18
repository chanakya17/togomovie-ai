package com.togomovie.togomovie_ai.service.impl;

import com.togomovie.togomovie_ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider movieTools;

    /** Per-conversation in-memory chat history */
    private final ConcurrentHashMap<String, MessageWindowChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public Flux<String> chat(String message, String conversationId) {
        String convId = conversationId != null ? conversationId : "default";
        log.debug("Chat request convId={} message={}", convId, message);

        MessageWindowChatMemory memory = memories.computeIfAbsent(
                convId, k -> MessageWindowChatMemory.builder().build());

        return chatClient.prompt()
                .user(message)
                .advisors(new MessageChatMemoryAdvisor(memory))
                .tools(movieTools)
                .stream()
                .content();
    }
}
