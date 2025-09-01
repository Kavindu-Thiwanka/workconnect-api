package com.workconnect.api.dto;

import com.workconnect.api.entity.EmployerProfile;
import lombok.Data;

@Data
public class EmployerProfileDto {
    private String companyName;
    private String companyDescription;
    private String location;
    private String companyLogoUrl;

    public static EmployerProfileDto fromEntity(EmployerProfile profile) {
        EmployerProfileDto dto = new EmployerProfileDto();
        dto.setCompanyName(profile.getCompanyName());
        dto.setCompanyDescription(profile.getCompanyDescription());
        dto.setLocation(profile.getLocation());
        dto.setCompanyLogoUrl(profile.getCompanyLogoUrl());
        return dto;
    }
}
