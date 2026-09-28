package com.java.etlflow.service.dataTransformer;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataTransformerImpl implements DataTransformer{
    @Override
    public List<Document> dataTransformer(List<Document> documents) {
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter
                .builder()
                .withChunkSize(300)
                .withMinChunkSizeChars(400)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();
        return tokenTextSplitter.apply(documents);
    }
}
