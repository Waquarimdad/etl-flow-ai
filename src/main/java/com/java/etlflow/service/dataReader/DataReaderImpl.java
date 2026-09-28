package com.java.etlflow.service.dataReader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DataReaderImpl implements DataReader {
    @Override
    public List<Document> loadFromPdf(MultipartFile file) {
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig
                .builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter
                        .builder()
                        .withNumberOfBottomTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build();
        PagePdfDocumentReader pagePdfDocumentReader =
                new PagePdfDocumentReader(file.getResource(), pdfDocumentReaderConfig);
        return pagePdfDocumentReader.read();
    }
}
