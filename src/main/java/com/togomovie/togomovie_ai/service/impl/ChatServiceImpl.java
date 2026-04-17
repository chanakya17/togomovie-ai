package com.togomovie.togomovie_ai.service.impl;

import com.togomovie.togomovie_ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ToolCallbackProvider movieTools;

    /** Per-conversation in-memory chat history */
    private final ConcurrentHashMap<String, InMemoryChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public Flux<String> chat(String message, String conversationId) {
        String convId = conversationId != null ? conversationId : "default";
        log.debug("Chat request convId={} message={}", convId, message);

        InMemoryChatMemory memory = memories.computeIfAbsent(convId, k -> new InMemoryChatMemory());

        return chatClient.prompt()
                .user(message)
                .advisors(
                        // RAG: retrieve relevant movie documents from pgvector
                        new QuestionAnswerAdvisor(vectorStore,
                                SearchRequest.builder().topK(5).build()),
                        // Multi-turn memory
                        new MessageChatMemoryAdvisor(memory)
                )
                .tools(movieTools)
                .stream()
                .content();
    }
}
