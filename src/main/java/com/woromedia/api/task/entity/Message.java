package com.woromedia.api.task.entity;

import lombok.Data;

import java.time.LocalDateTime;

import javax.persistence.*;

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
    private LocalDateTime time;
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
}
