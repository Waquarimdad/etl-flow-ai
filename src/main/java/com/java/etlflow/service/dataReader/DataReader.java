package com.java.etlflow.service.dataReader;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataReader {
    List<Document> loadFromPdf(MultipartFile file);
}
