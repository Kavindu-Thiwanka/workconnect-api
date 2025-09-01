package com.workconnect.api.repository;

import com.workconnect.api.entity.EmployerProfile;
import com.workconnect.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    
    /**
     * Find employer profile by user
     */
    Optional<EmployerProfile> findByUser(User user);
    
    /**
     * Find employer profile by user email
     */
    Optional<EmployerProfile> findByUser_Email(String email);
    
    /**
     * Check if employer profile exists for user
     */
    boolean existsByUser(User user);
}
