package com.java.etlflow.dto;


public class AiDto {
    private String prompt;
    private String conversationId;

    public AiDto() {
    }

    public AiDto(String prompt, String conversationId) {
        this.prompt = prompt;
        this.conversationId = conversationId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "AiDto{" +
                "prompt='" + prompt + '\'' +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }
}
