package com.workconnect.api.service;

import com.workconnect.api.dto.WorkerApplicationDto;
import java.util.List;

public interface WorkerService {
    List<WorkerApplicationDto> getApplicationsForWorker(String workerEmail);
}
