package com.woromedia.api.task.dto;

import java.time.LocalDateTime;

public class UserWithLastMessageDto {
    private Long userId;
    private String username;
    private String lastMessage;
    private LocalDateTime messageTime;
    private Boolean isRead;

    public UserWithLastMessageDto(Long userId, String username, String lastMessage, LocalDateTime messageTime, Boolean isRead) {
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.messageTime = messageTime;
        this.isRead = isRead;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }
}
