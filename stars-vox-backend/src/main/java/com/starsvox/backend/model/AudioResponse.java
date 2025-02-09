package com.starsvox.backend.model;

public class AudioResponse {
    private String audioFile;
    private String errorMessage;

    public AudioResponse(String audioFile, String errorMessage) {
        this.audioFile = audioFile;
        this.errorMessage = errorMessage;
    }

    // ... getters
    public String getAudioFile() {
        return audioFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}