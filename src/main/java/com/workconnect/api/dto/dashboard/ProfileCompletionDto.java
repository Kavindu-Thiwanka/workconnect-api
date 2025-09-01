package com.workconnect.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Profile Completion DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCompletionDto {
    private double percentage;
    private List<String> missingFields;
    private List<String> tips;
}
