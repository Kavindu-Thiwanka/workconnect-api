package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.Role;
import com.workconnect.api.constants.Enum.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminUserDto {
    private Long userId;
    private String email;
    private Role role;
    private UserStatus status;
    private String profileType;
    private String displayName;
    private String location;
    private String phoneNumber;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private int totalApplications;
    private int totalJobPostings;
    private Double averageRating;
}
