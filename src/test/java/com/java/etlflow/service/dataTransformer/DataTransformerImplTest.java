package com.java.etlflow.service.dataTransformer;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DataTransformerImplTest {
    private final DataTransformerImpl dataTransformer = new DataTransformerImpl();

    @Test
    void splitsExtractedDocumentsIntoEmbeddableChunks() {
        List<Document> chunks = dataTransformer.dataTransformer(
                List.of(new Document("Sample document content. ".repeat(100))));

        assertFalse(chunks.isEmpty());
    }
}
