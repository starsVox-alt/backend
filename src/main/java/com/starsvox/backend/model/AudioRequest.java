package com.starsvox.backend.model;

public class AudioRequest {
    private String prompt;

    public AudioRequest() {
    }

    public AudioRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}