package com.workconnect.api.dto;

import lombok.Data;

@Data
public class ApplicantDto {
    private Long workerId;
    private String firstName;
    private String lastName;
    private String skills;
}
