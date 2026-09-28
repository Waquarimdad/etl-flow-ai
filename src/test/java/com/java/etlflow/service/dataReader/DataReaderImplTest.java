package com.java.etlflow.service.dataReader;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DataReaderImplTest {
    private final DataReaderImpl dataReader = new DataReaderImpl();

    @Test
    void readsUploadedPdfResource() throws IOException {
        byte[] pdf;
        try (var inputStream = new ClassPathResource("Resume.pdf").getInputStream()) {
            pdf = inputStream.readAllBytes();
        }
        MockMultipartFile file = new MockMultipartFile("file", "Resume.pdf", "application/pdf", pdf);

        List<Document> documents = dataReader.loadFromPdf(file);

        assertFalse(documents.isEmpty());
    }
}
