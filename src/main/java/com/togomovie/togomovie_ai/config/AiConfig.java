package com.togomovie.togomovie_ai.config;

import com.togomovie.togomovie_ai.mcp.MovieMcpTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /**
     * Default ChatClient with system prompt for movie recommendations.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are a helpful movie assistant for the Togo Movie platform.
                        You help users discover movies, get recommendations, and answer
                        questions about films. Be concise, friendly, and accurate.
                        When searching for movies, always use the available tools.
                        """)
                .build();
    }

    /**
     * Expose MovieMcpTools @Tool methods to the MCP server and ChatClient.
     */
    @Bean
    public ToolCallbackProvider movieTools(MovieMcpTools movieMcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(movieMcpTools)
                .build();
    }
}
