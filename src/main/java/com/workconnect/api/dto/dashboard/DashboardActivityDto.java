package com.workconnect.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Dashboard Activity Item DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardActivityDto {
    private String type; // APPLICATION_SUBMITTED, JOB_POSTED, PROFILE_VIEWED, etc.
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private String actionUrl;
    private String iconName;
}
