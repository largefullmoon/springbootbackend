package com.woromedia.api.task.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long receiverId;
    private Long senderId;
    private String message;
    private String filename;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;  // Keep as LocalDateTime for database compatibility
    private Boolean isRead;

    public Message() {
    }

    public Message(String message, Long senderId, Long receiverId, LocalDateTime time, String filename, boolean isRead) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
        this.filename = filename;
        this.isRead = isRead;
    }

    // Optional method to convert to ZonedDateTime if needed
    public ZonedDateTime getZonedTime() {
        return time != null ? time.atZone(java.time.ZoneId.systemDefault()) : null;
    }
}
