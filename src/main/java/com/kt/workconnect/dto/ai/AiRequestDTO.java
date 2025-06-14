package com.kt.workconnect.dto.ai;

import lombok.Data;
import java.util.List;

@Data
public class AiRequestDTO {
    private AiWorkerProfileDTO worker_profile;
    private List<AiJobDTO> jobs;
}
