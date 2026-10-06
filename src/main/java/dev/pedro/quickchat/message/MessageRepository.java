package dev.pedro.quickchat.message;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

   @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND (:beforeId IS NULL OR m.id < :beforeId) ORDER BY m.createdAt ASC")
    List<Message> findByChatId(String chatId, Long beforeId, Pageable pageable);
}
