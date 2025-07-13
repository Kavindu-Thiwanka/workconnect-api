package com.kt.workconnect.dto.ai;

import lombok.Data;
import java.util.UUID;

@Data
public class AiJobDTO {
    private UUID id;
    private String title;
    private String description;
    private String requiredSkills;
}
