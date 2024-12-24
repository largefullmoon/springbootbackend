package com.woromedia.api.task.controller;

import com.woromedia.api.task.dto.MessageDto;
import com.woromedia.api.task.dto.UserWithLastMessageDto;
import com.woromedia.api.task.entity.Message;
import com.woromedia.api.task.entity.Role;
import com.woromedia.api.task.entity.User;
import com.woromedia.api.task.payload.SignUpDTO;
import com.woromedia.api.task.repository.MessageRepository;
import com.woromedia.api.task.repository.RoleRepository;
import com.woromedia.api.task.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api")

public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For sending messages to WebSocket clients

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/users")
    public List<UserWithLastMessageDto> getAllUsers(@RequestParam Long myId) {
        List<User> users = userRepository.findAll();
        return users.stream().filter(user -> user.getId() != myId).map(user -> {
            Message lastMessage = messageRepository.findLatestMessageWithUser(myId, user.getId())
                    .stream()
                    .findFirst() // Get the last message
                    .orElse(null);
            
            return new UserWithLastMessageDto(
                user.getId(), 
                user.getUsername(),
                lastMessage != null ? lastMessage.getMessage() : null,
                lastMessage != null ? lastMessage.getTime() : null,
                lastMessage != null ? lastMessage.getIsRead() : null
            );
        }).collect(Collectors.toList());
    }

    private final MessageRepository messageRepository;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    public ChatController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Create uploads directory if it does not exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file to the upload directory
            String originalFilename = file.getOriginalFilename();
            String filePath = UPLOAD_DIR + originalFilename;

            file.transferTo(new File(filePath));

            // Return the filename to the frontend
            return ResponseEntity.ok(originalFilename);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageDto handleMessage(@Payload MessageDto messageDto) {
        System.out.println("Received message through WebSocket: " + messageDto.getMessage());
        Message message = new Message(messageDto.getMessage(), messageDto.getSenderId(), messageDto.getReceiverId(),
                messageDto.getTime(), messageDto.getFileName(), false);
        messageRepository.save(message);
        return messageDto;
    }

    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        // Use ZonedDateTime.now() with system default zone
        ZonedDateTime serverTime = ZonedDateTime.now();
        System.out.println("Message: " + messageDto.getMessage());
        System.out.println("Sender ID: " + messageDto.getSenderId());
        System.out.println("Receiver ID: " + messageDto.getReceiverId());
        System.out.println("Time: " + serverTime);
        System.out.println("UTC Time: " + serverTime.withZoneSameInstant(ZoneOffset.UTC));
        System.out.println("FileName: " + messageDto.getFileName());
        
        // Convert ZonedDateTime to LocalDateTime for database storage if needed
        Message message = new Message(
            messageDto.getMessage(), 
            messageDto.getSenderId(), 
            messageDto.getReceiverId(),
            serverTime.toLocalDateTime(), 
            messageDto.getFileName(), 
            false
        );
        
        messageRepository.save(message);
        
        // Set the time in the DTO to ensure consistent timezone
        messageDto.setTime(serverTime);
        
        // Emit socket event to the frontend with ZonedDateTime
        messagingTemplate.convertAndSend("/topic/messages", messageDto);
        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        try {
            List<Message> messages = messageRepository.findMessagesBetweenUsers(senderId, receiverId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @RequestBody MessageDto messageDto) {
        // Check if the message exists
        return messageRepository.findById(id)
                .map(existingMessage -> {
                    // Update fields
                    existingMessage.setIsRead(true);
                    // Save updated message
                    messageRepository.save(existingMessage);
                    return ResponseEntity.ok("Message updated successfully!");
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Message not found!"));
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            File file = new File(UPLOAD_DIR + filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving file");
        }
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            default:
                return org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}
