package com.workconnect.api.controller;

import com.workconnect.api.dto.WorkerApplicationDto;
import com.workconnect.api.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/worker")
@PreAuthorize("hasRole('WORKER')")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping("/applications")
    public ResponseEntity<List<WorkerApplicationDto>> getMyApplications(Principal principal) {
        List<WorkerApplicationDto> applications = workerService.getApplicationsForWorker(principal.getName());
        return ResponseEntity.ok(applications);
    }
}
