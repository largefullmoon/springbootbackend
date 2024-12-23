package com.woromedia.api.task.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private String message;
    private Long receiverId;
    private Long senderId;
    private LocalDateTime time;
    private String filename;

    public MessageDto() {
    }

    public MessageDto(String message, Long senderId, Long receiverId, LocalDateTime time, String filename) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
        this.filename = filename;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getFileName() {
        return filename;
    }

    // Setters (optional if you're only using constructors)
    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }
}
