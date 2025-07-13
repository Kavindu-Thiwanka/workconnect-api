package com.workconnect.api.dto;

import lombok.Data;

@Data
public class WorkerProfileDto {
    private String firstName;
    private String lastName;
    private String location;
    private String skills;
    private String experience;
    private String availability;
}
