package com.workconnect.api.repository;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.entity.JobApplication;
import com.workconnect.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByWorker_UserIdAndJobPosting_Id(Long workerId, Long jobId);

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
}
