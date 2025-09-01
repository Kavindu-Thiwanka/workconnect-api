package com.workconnect.api.repository;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.entity.JobApplication;
import com.workconnect.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByWorker_UserIdAndJobPosting_Id(Long workerId, Long jobId);

    Optional<JobApplication> findByWorker_UserIdAndJobPosting_Id(Long workerId, Long jobId);

    List<JobApplication> findByJobPosting_Id(Long jobId);

    /**
     * Counts all applications for a specific worker that have a specific status.
     * Spring Data JPA creates the query automatically from this method name.
     *
     * @param worker The user (worker) to count applications for.
     * @param status The status to match (e.g., COMPLETED).
     * @return The number of matching applications.
     */
    long countByWorkerAndStatus(User worker, JobApplicationStatus status);

    List<JobApplication> findByWorker_Email(String email);

    /**
     * Counts all applications for a specific job posting.
     * Spring Data JPA creates the query automatically from this method name.
     *
     * @param jobId The job posting ID to count applications for.
     * @return The number of applications for the job.
     */
    long countByJobPosting_Id(Long jobId);

    // Admin-specific queries
    long countByStatus(JobApplicationStatus status);
    Long countByWorker_UserId(Long workerId);
    Long countByJobPosting_IdAndStatus(Long jobId, JobApplicationStatus status);

    Page<JobApplication> findByJobPosting_Id(Long jobId, Pageable pageable);
    Page<JobApplication> findByWorker_UserId(Long workerId, Pageable pageable);

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.id > (SELECT MAX(ja2.id) - 100 FROM JobApplication ja2)")
    Long countByCreatedAtAfter(@Param("createdAt") LocalDateTime createdAt);
}
