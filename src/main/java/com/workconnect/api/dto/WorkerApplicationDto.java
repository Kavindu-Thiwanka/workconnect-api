package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.entity.JobApplication;
import lombok.Data;

@Data
public class WorkerApplicationDto {
    private Long applicationId;
    private JobApplicationStatus status;
    private String jobTitle;
    private String companyName;
    private Long jobId;

    public static WorkerApplicationDto fromEntity(JobApplication application) {
        WorkerApplicationDto dto = new WorkerApplicationDto();
        dto.setApplicationId(application.getId());
        dto.setStatus(application.getStatus());
        dto.setJobId(application.getJobPosting().getId());
        dto.setJobTitle(application.getJobPosting().getJobTitle());

        if (application.getJobPosting().getEmployer().getProfile() instanceof com.workconnect.api.entity.EmployerProfile) {
            dto.setCompanyName(((com.workconnect.api.entity.EmployerProfile) application.getJobPosting().getEmployer().getProfile()).getCompanyName());
        }
        return dto;
    }
}
