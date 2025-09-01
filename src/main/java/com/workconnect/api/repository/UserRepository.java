package com.workconnect.api.repository;

import com.workconnect.api.constants.Enum.Role;
import com.workconnect.api.constants.Enum.UserStatus;
import com.workconnect.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address. Spring Data JPA automatically
     * implements this method based on its name.
     *
     * @param email the email to search for
     * @return an Optional containing the User if found, or an empty Optional
     */
    Optional<User> findByEmail(String email);

    // Admin-specific queries
    long countByRole(Role role);
    long countByStatus(UserStatus status);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userId > (SELECT MAX(u2.userId) - 100 FROM User u2)")
    Long countByCreatedAtAfter(@Param("createdAt") LocalDateTime createdAt);
}
