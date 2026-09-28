package com.java.etlflow.service;

import com.java.etlflow.dto.AiDto;
import com.java.etlflow.repository.VectorStoreRepository;
import com.java.etlflow.service.dataReader.DataReader;
import com.java.etlflow.service.dataTransformer.DataTransformer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {
    private final ChatClient googleChatClient;
    private final RetrievalAugmentationAdvisor ragAdvisor;
    private final DataReader dataReader;
    private final DataTransformer dataTransformer;
    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;

    private static final String SYSTEM_PROMPT = """
            Act as an expert AI assistant. Your primary goal is to provide accurate, concise, and helpful answers.

            Guidelines:
            1. Always analyze the user request to determine if real-time or external information is needed.
            2. If real-time external data is required, call the provided tools before generating a response.
            3. Synthesize tool results into a clear, formatted answer. Do not output raw JSON data.
            4. If a query does not require real-time data, answer directly using your general knowledge.
            """;

    public AiServiceImpl(@Qualifier("googleChatClient") ChatClient googleChatClient,
                         @Qualifier("ragAdvisor") RetrievalAugmentationAdvisor ragAdvisor,
                         DataReader dataReader,
                         DataTransformer dataTransformer,
                         VectorStore vectorStore,
                         VectorStoreRepository vectorStoreRepository) {
        this.googleChatClient = googleChatClient;
        this.ragAdvisor = ragAdvisor;
        this.dataReader = dataReader;
        this.dataTransformer = dataTransformer;
        this.vectorStore = vectorStore;
        this.vectorStoreRepository = vectorStoreRepository;
    }

    @Override
    public String aiChat(AiDto request) {
        return googleChatClient
                .prompt()
                .user(promptUserSpec -> promptUserSpec.text(request.getPrompt()))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, request.getConversationId())
                        .advisors(ragAdvisor))
                .system(promptSystemSpec -> promptSystemSpec.text(SYSTEM_PROMPT))
                .call()
                .content();
    }

    @Override
    @Transactional
    public String processFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        List<Document> documents = dataReader.loadFromPdf(file);
        if (documents.isEmpty()) {
            throw new IllegalArgumentException("No text could be extracted from the uploaded PDF.");
        }

        List<Document> chunks = dataTransformer.dataTransformer(documents);
        if (chunks.isEmpty()) {
            throw new IllegalArgumentException("No text could be extracted from the uploaded PDF.");
        }

        vectorStoreRepository.deleteAll();
        vectorStore.add(chunks);
        return "Document indexed successfully: " + chunks.size() + " chunks.";
    }
}
