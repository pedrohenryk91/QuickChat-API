package dev.pedro.quickchat.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = """
        SELECT u FROM User u 
        WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :query, '%')))
        ORDER BY
            CASE
                WHEN LOWER(u.username) = LOWER(:query) THEN 1
                WHEN LOWER(u.username) LIKE LOWER(CONCAT(:query, '%')) THEN 2
                WHEN LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) THEN 3
                ELSE 4
            END ASC,
            u.username ASC
        """,
        countQuery = """
        SELECT COUNT(u) FROM User u
        WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<User> searchWithUsernamePriority(
        @Param("query") String query,
        Pageable pageable
    );

    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
