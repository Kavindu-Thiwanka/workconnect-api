package com.workconnect.api.repository;

import com.workconnect.api.entity.Review;
import com.workconnect.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews where the specified user is the one being reviewed.
     * Spring Data JPA creates the query automatically from this method name.
     *
     * @param reviewee The user who was reviewed.
     * @return A list of matching reviews.
     */
    List<Review> findByReviewee(User reviewee);

}
