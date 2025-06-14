package com.kt.workconnect.dto;

import com.kt.workconnect.constant.Enum.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole userRole;
}
