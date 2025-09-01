package com.workconnect.api.repository;

import com.workconnect.api.entity.User;
import com.workconnect.api.entity.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {
    
    /**
     * Find worker profile by user
     */
    Optional<WorkerProfile> findByUser(User user);
    
    /**
     * Find worker profile by user email
     */
    Optional<WorkerProfile> findByUser_Email(String email);
    
    /**
     * Check if worker profile exists for user
     */
    boolean existsByUser(User user);
}
