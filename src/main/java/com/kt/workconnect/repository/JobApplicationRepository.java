package com.kt.workconnect.repository;

import com.kt.workconnect.entity.Job;
import com.kt.workconnect.entity.JobApplication;
import com.kt.workconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findByJob(Job job);

    List<JobApplication> findAllByApplicant(User worker);
}
