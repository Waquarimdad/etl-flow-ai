package com.java.etlflow.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatMemory chatMemory() {
        InMemoryChatMemoryRepository inMemoryChatMemoryRepository = new InMemoryChatMemoryRepository();

        return MessageWindowChatMemory.builder().chatMemoryRepository(inMemoryChatMemoryRepository).build();
    }

    @Bean("googleChatClient")
    public ChatClient googleChatClient(GoogleGenAiChatModel googleGenAiChatModel, ChatMemory chatMemory) {
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();

        return ChatClient
                .builder(googleGenAiChatModel)
                .defaultOptions(GoogleGenAiChatOptions
                        .builder()
                        .model("gemini-3.1-flash-lite")
                        .temperature(0.3)
                        .maxTokens(1000))
                .defaultAdvisors(messageChatMemoryAdvisor, new SimpleLoggerAdvisor())
                .build();
    }
    @Bean("ollamaChatClient")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient
                .builder(ollamaChatModel)
                .defaultOptions(OllamaChatOptions
                        .builder()
                        .model("codellama:latest")
                        .temperature(0.3)
                        .maxTokens(1000))
                .build();
    }
}
