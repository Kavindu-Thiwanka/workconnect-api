package com.workconnect.api.dto.dashboard;

import com.workconnect.api.dto.JobApplicationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Recent Applications DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentApplicationsDto {
    private List<JobApplicationDto> applications;
    private int totalCount;
}
