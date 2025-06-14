package com.kt.workconnect.dto.ai;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AiResponseDTO {
    private List<UUID> recommended_job_ids;
}
