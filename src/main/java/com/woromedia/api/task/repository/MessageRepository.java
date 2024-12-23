package com.woromedia.api.task.repository;

import com.woromedia.api.task.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(Long receiverId);

    List<Message> findBySenderId(Long senderId);

    @Query(value = "SELECT m.* " +
            "FROM messages m " +
            "INNER JOIN (SELECT " +
            "                 CASE WHEN m1.sender_id = :myId THEN m1.receiver_id ELSE m1.sender_id END AS user_id, " +
            "                 MAX(m1.time) AS max_time " +
            "             FROM messages m1 " +
            "             WHERE m1.sender_id = :myId OR m1.receiver_id = :userId " +
            "              OR m1.sender_id = :userId OR m1.receiver_id = :myId " +
            "             GROUP BY user_id) latest " +
            "ON (m.sender_id = :myId AND m.receiver_id = :userId OR " +
            "    m.receiver_id = :myId AND m.sender_id = :userId) " +
            "AND m.time = latest.max_time", nativeQuery = true)
    List<Message> findLatestMessageWithUser(@Param("myId") Long myId, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM messages m " +
           "WHERE (m.sender_id = :senderId AND m.receiver_id = :receiverId) OR " +
           "(m.sender_id = :receiverId AND m.receiver_id = :senderId) " +
           "ORDER BY m.time ASC", nativeQuery = true)
    List<Message> findMessagesBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}