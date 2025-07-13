package com.workconnect.api.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class WorkerProfileDto {
    private String firstName;
    private String lastName;
    private String location;
    private String experience;
    private String availability;
    private Set<String> skills = new HashSet<>();
}
