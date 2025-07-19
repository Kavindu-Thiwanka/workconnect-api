package com.workconnect.api.service;

import com.workconnect.api.dto.EmployerProfileDto;
import com.workconnect.api.dto.WorkerProfileDto;

public interface ProfileService {

    Object getProfile(String email);

    WorkerProfileDto updateWorkerProfile(String email, WorkerProfileDto workerProfileDto);

    EmployerProfileDto updateEmployerProfile(String email, EmployerProfileDto employerProfileDto);

    void updateProfilePicture(String email, String imageUrl);

}
