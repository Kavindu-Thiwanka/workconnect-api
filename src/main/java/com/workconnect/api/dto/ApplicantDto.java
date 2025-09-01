package com.workconnect.api.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ApplicantDto {
    private Long workerId;
    private String firstName;
    private String lastName;
    private Set<String> skills;
}
