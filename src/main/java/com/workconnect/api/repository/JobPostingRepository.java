package com.workconnect.api.repository;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(JobStatus status);

    List<JobPosting> findByStatusAndJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            JobStatus status, String titleKeyword, String descriptionKeyword
    );

    List<JobPosting> findByEmployer_Email(String employerEmail);

    // Admin-specific queries
    long countByStatus(JobStatus status);
    Long countByEmployer_UserId(Long employerId);

    Page<JobPosting> findByJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword, String descriptionKeyword, Pageable pageable
    );

    @Query("SELECT COUNT(j) FROM JobPosting j WHERE j.id > (SELECT MAX(j2.id) - 100 FROM JobPosting j2)")
    Long countByCreatedAtAfter(@Param("createdAt") LocalDateTime createdAt);
}
