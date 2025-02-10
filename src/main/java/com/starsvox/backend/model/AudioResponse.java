package com.starsvox.backend.model;

public class AudioResponse {
    private String taskId;
    private String message;

    public AudioResponse() {
    }

    public AudioResponse(String taskId, String message) {
        this.taskId = taskId;
        this.message = message;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}