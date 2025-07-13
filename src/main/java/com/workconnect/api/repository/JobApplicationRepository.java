package com.workconnect.api.repository;

import com.workconnect.api.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByWorker_IdAndJobPosting_Id(Long workerId, Long jobId);

    List<JobApplication> findByJobPosting_Id(Long jobId);
}
