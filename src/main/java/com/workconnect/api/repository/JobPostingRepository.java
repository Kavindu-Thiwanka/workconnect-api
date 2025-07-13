package com.workconnect.api.repository;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(JobStatus status);

    List<JobPosting> findByStatusAndJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            JobStatus status, String titleKeyword, String descriptionKeyword
    );
}
