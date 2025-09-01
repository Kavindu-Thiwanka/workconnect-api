package com.workconnect.api.dto;

import com.workconnect.api.entity.Badge;
import lombok.Data;

@Data
public class BadgeDto {
    private String displayName;
    private String description;
    private String iconUrl;

    public static BadgeDto fromEntity(Badge badge) {
        BadgeDto dto = new BadgeDto();
        dto.setDisplayName(badge.getDisplayName());
        dto.setDescription(badge.getDescription());
        dto.setIconUrl(badge.getIconUrl());
        return dto;
    }
}
