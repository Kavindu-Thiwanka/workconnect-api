package com.kt.workconnect.repository;

import com.kt.workconnect.entity.Review;
import com.kt.workconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByReviewee(User reviewee);
}
