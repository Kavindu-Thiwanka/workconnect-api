package com.workconnect.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Worker Statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerStatsDto {
    private int totalApplications;
    private int pendingApplications;
    private int interviewsScheduled;
    private int profileViews;
    private int jobMatchesThisWeek;
    private double averageResponseTime; // in days
}
