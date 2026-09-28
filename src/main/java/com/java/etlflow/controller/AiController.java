package com.java.etlflow.controller;

import com.java.etlflow.dto.AiDto;
import com.java.etlflow.service.AiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/llm")
public class AiController {
    private final AiService service;

    public AiController(AiService service) {
        this.service = service;
    }

    @PostMapping("/ai-chat")
    public String aiChat(@RequestBody AiDto request) {
        return service.aiChat(request);
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("File cannot be empty");
        }

        String result = service.processFile(file);

        return ResponseEntity.ok(result);
    }
}
