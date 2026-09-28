package com.java.etlflow.service;

import com.java.etlflow.service.dataReader.DataReader;
import com.java.etlflow.service.dataTransformer.DataTransformer;
import com.java.etlflow.repository.VectorStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {
    @Mock
    private ChatClient googleChatClient;
    @Mock
    private RetrievalAugmentationAdvisor ragAdvisor;
    @Mock
    private DataReader dataReader;
    @Mock
    private DataTransformer dataTransformer;
    @Mock
    private VectorStore vectorStore;
    @Mock
    private VectorStoreRepository vectorStoreRepository;

    private AiServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AiServiceImpl(
                googleChatClient, ragAdvisor, dataReader, dataTransformer, vectorStore, vectorStoreRepository);
    }

    @Test
    void processFileReadsTransformsAndIndexesDocuments() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "pdf content".getBytes());
        List<Document> pages = List.of(new Document("page content"));
        List<Document> chunks = List.of(new Document("chunk content"));
        when(dataReader.loadFromPdf(file)).thenReturn(pages);
        when(dataTransformer.dataTransformer(pages)).thenReturn(chunks);

        String result = service.processFile(file);

        assertEquals("Document indexed successfully: 1 chunks.", result);
        var inOrder = inOrder(vectorStoreRepository, vectorStore);
        inOrder.verify(vectorStoreRepository).deleteAll();
        inOrder.verify(vectorStore).add(chunks);
    }

    @Test
    void processFileRejectsEmptyUploads() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> service.processFile(file));

        verifyNoInteractions(dataReader, dataTransformer, vectorStore, vectorStoreRepository);
    }

    @Test
    void processFileDoesNotIndexWhenNoDocumentsAreExtracted() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "pdf content".getBytes());
        when(dataReader.loadFromPdf(file)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> service.processFile(file));

        verify(dataTransformer, never()).dataTransformer(org.mockito.ArgumentMatchers.anyList());
        verifyNoInteractions(vectorStore, vectorStoreRepository);
    }
}
