package com.java.etlflow.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    private final VectorStore vectorStore;
    private final ChatClient ollamaChatClient;

    public RagConfig(VectorStore vectorStore,
                     @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.vectorStore = vectorStore;
        this.ollamaChatClient = ollamaChatClient;
    }

    @Bean("ragAdvisor")
    public RetrievalAugmentationAdvisor ragAdvisor() {

        return RetrievalAugmentationAdvisor
                .builder()
                .queryTransformers(
                        RewriteQueryTransformer
                                .builder()
                                .chatClientBuilder(ollamaChatClient.mutate().clone())
                                .build())
                .queryExpander(MultiQueryExpander
                        .builder()
                        .chatClientBuilder(ollamaChatClient.mutate().clone())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever
                        .builder()
                        .topK(3)
                        .similarityThreshold(0.1)
                        .vectorStore(vectorStore)
                        .build())
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().allowEmptyContext(true).build())
                .build();
    }
}
