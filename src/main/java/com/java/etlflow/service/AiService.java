package com.java.etlflow.service;

import com.java.etlflow.dto.AiDto;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {
    String aiChat(AiDto request);

    String processFile(MultipartFile file);
}
