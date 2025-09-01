package com.workconnect.api.service.impl;

import com.workconnect.api.dto.WorkerApplicationDto;
import com.workconnect.api.entity.JobApplication;
import com.workconnect.api.repository.JobApplicationRepository;
import com.workconnect.api.service.WorkerService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerServiceImpl implements WorkerService {

    private final JobApplicationRepository applicationRepository;

    public WorkerServiceImpl(JobApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<WorkerApplicationDto> getApplicationsForWorker(String workerEmail) {
        List<JobApplication> applications = applicationRepository.findByWorker_Email(workerEmail);
        return applications.stream()
                .map(WorkerApplicationDto::fromEntity)
                .collect(Collectors.toList());
    }
}
