package com.woromedia.api.task.dto;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;

public class MessageDto {
    private String message;
    private Long receiverId;
    private Long senderId;
    private ZonedDateTime time;
    private String filename;

    public MessageDto() {
    }

    public MessageDto(String message, Long senderId, Long receiverId, ZonedDateTime time, String filename) {
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

    public ZonedDateTime getTime() {
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

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    // Optional: Add a method to get UTC time explicitly
    public ZonedDateTime getUtcTime() {
        return time.withZoneSameInstant(ZoneOffset.UTC);
    }
}
