package com.workconnect.api.service.impl;

import com.workconnect.api.dto.EmployerProfileDto;
import com.workconnect.api.dto.WorkerProfileDto;
import com.workconnect.api.entity.EmployerProfile;
import com.workconnect.api.entity.Profile;
import com.workconnect.api.entity.User;
import com.workconnect.api.entity.WorkerProfile;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.ProfileService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Object getProfile(String email) {
        User user = findUserByEmail(email);
        Profile profile = user.getProfile();

        if (profile instanceof WorkerProfile) {
            return mapToWorkerDto((WorkerProfile) profile);
        } else if (profile instanceof EmployerProfile) {
            return mapToEmployerDto((EmployerProfile) profile);
        }
        return null;
    }

    @Transactional
    @Override
    public WorkerProfileDto updateWorkerProfile(String email, WorkerProfileDto workerProfileDto) {
        User user = findUserByEmail(email);
        if (!(user.getProfile() instanceof WorkerProfile)) {
            throw new IllegalStateException("User is not a worker.");
        }
        WorkerProfile workerProfile = (WorkerProfile) user.getProfile();

        workerProfile.setFirstName(workerProfileDto.getFirstName());
        workerProfile.setLastName(workerProfileDto.getLastName());
        workerProfile.setLocation(workerProfileDto.getLocation());
        workerProfile.setSkills(workerProfileDto.getSkills());
        workerProfile.setExperience(workerProfileDto.getExperience());
        workerProfile.setAvailability(workerProfileDto.getAvailability());

        userRepository.save(user);
        return mapToWorkerDto(workerProfile);
    }

    @Transactional
    @Override
    public EmployerProfileDto updateEmployerProfile(String email, EmployerProfileDto employerProfileDto) {
        User user = findUserByEmail(email);
        if (!(user.getProfile() instanceof EmployerProfile)) {
            throw new IllegalStateException("User is not an employer.");
        }
        EmployerProfile employerProfile = (EmployerProfile) user.getProfile();

        employerProfile.setCompanyName(employerProfileDto.getCompanyName());
        employerProfile.setCompanyDescription(employerProfileDto.getCompanyDescription());
        employerProfile.setLocation(employerProfileDto.getLocation());

        userRepository.save(user);
        return mapToEmployerDto(employerProfile);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private WorkerProfileDto mapToWorkerDto(WorkerProfile profile) {
        WorkerProfileDto dto = new WorkerProfileDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setLocation(profile.getLocation());
        dto.setSkills(profile.getSkills());
        dto.setExperience(profile.getExperience());
        dto.setAvailability(profile.getAvailability());
        return dto;
    }

    private EmployerProfileDto mapToEmployerDto(EmployerProfile profile) {
        EmployerProfileDto dto = new EmployerProfileDto();
        dto.setCompanyName(profile.getCompanyName());
        dto.setCompanyDescription(profile.getCompanyDescription());
        dto.setLocation(profile.getLocation());
        return dto;
    }
}
