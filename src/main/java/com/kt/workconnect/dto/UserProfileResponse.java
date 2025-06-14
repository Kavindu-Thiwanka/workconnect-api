package com.kt.workconnect.dto;

import com.kt.workconnect.constant.Enum.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserProfileResponse {
    private UUID id;
    private String email;
    private UserRole userRole;
    private LocalDateTime createdAt;
}
