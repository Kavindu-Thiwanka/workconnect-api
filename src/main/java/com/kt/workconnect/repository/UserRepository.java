package com.kt.workconnect.repository;

import com.kt.workconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email address.
     * Spring Data JPA automatically creates the implementation for this method
     * based on its name.
     *
     * @param email the user's email
     * @return an Optional containing the User if found, otherwise empty
     */
    Optional<User> findByEmail(String email);
}
