package com.workconnect.api.repository;

import com.workconnect.api.entity.JobImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobImageRepository extends JpaRepository<JobImage, Long> {
}
