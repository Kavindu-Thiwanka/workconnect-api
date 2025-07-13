package com.workconnect.api.repository;

import com.workconnect.api.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByWorker_IdAndJobPosting_Id(Long workerId, Long jobId);
}
