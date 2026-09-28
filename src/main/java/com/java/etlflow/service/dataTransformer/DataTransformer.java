package com.java.etlflow.service.dataTransformer;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DataTransformer {
    List<Document> dataTransformer(List<Document> documents);
}
