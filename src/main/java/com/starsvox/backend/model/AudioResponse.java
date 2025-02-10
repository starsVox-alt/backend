package com.starsvox.backend.model;

import lombok.Data;

@Data
public class AudioResponse {
    private String taskId; // audioFileの代わりにtaskId
    private String errorMessage;

    public AudioResponse(String taskId, String errorMessage) {
        this.taskId = taskId;
        this.errorMessage = errorMessage;
    }
}