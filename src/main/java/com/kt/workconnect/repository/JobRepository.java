package com.kt.workconnect.repository;

import com.kt.workconnect.entity.Job;
import com.kt.workconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByPostedBy(User user);
}
