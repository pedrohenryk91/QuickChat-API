package dev.pedro.quickchat.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.annotation.Nullable;


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

    List<Chat> findByType(ChatType type, Pageable pageable);

    @Query("""
        SELECT c FROM Chat c 
        JOIN c.members m 
        WHERE m.id = :userId 
        AND (
            cast(:beforeDate as timestamp) IS NULL 
            OR c.updatedAt < :beforeDate 
            OR (c.updatedAt = :beforeDate AND c.id < :beforeId)
        )
        ORDER BY c.updatedAt DESC, c.id DESC
    """)
    List<Chat> findByMembersId(
        @Param("userId") String userId,
        @Param("beforeDate") @Nullable LocalDateTime beforeDate,
        @Param("beforeId") @Nullable String beforeId,
        Pageable pageable
    );

  //  List<Chat> searchChats(String query, Pageable pageable);
}
