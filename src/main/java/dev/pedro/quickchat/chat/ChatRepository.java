package dev.pedro.quickchat.chat;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRepository extends JpaRepository<Chat, String>{

    @Query("""
        SELECT c FROM Chat c
        JOIN c.members m1
        JOIN c.members m2
        WHERE c.type = dev.pedro.quickchat.chat.ChatType.DIRECT
          AND m1.id = :userAId
          AND m2.id = :userBId        
    """)
    Optional<Chat> findDirectChatBetweenUsers(@Param("userAId") String userAId, @Param("userBId") String userBId);

    Page<Chat> findByType(ChatType type, Pageable pageable);

    Page<Chat> findByMembersId(String userId, Pageable pageable);
}
