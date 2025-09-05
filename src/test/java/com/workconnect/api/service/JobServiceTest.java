package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.constants.Enum.JobType;
import com.workconnect.api.constants.Enum.Role;
import com.workconnect.api.constants.Enum.UserStatus;
import com.workconnect.api.dto.CreateJobRequestDto;
import com.workconnect.api.entity.JobPosting;
import com.workconnect.api.entity.User;
import com.workconnect.api.repository.JobApplicationRepository;
import com.workconnect.api.repository.JobImageRepository;
import com.workconnect.api.repository.JobPostingRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private JobImageRepository jobImageRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private User employer;
    private CreateJobRequestDto jobDto;

    @BeforeEach
    void setUp() {
        employer = new User();
        employer.setUserId(1L);
        employer.setEmail("employer@test.com");
        employer.setRole(Role.EMPLOYER);
        employer.setStatus(UserStatus.ACTIVE);

        jobDto = new CreateJobRequestDto();
        jobDto.setJobTitle("Software Engineer");
        jobDto.setDescription("Develop amazing software");
        jobDto.setRequiredSkills("Java, Spring Boot, React");
        jobDto.setLocation("New York, NY");
        jobDto.setSalary(75000.0);
        jobDto.setJobType(JobType.CONTRACT);
        jobDto.setStartDate(LocalDate.now().plusDays(7));
        jobDto.setEndDate(LocalDate.now().plusDays(37));
    }

    @Test
    void createJob_ShouldSetAllFields_IncludingSalaryLocationAndSkills() {
        // Given
        when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employer));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
            JobPosting job = invocation.getArgument(0);
            job.setId(1L);
            return job;
        });

        // When
        JobPosting result = jobService.createJob("employer@test.com", jobDto);

        // Then
        assertNotNull(result);
        assertEquals("Software Engineer", result.getJobTitle());
        assertEquals("Develop amazing software", result.getDescription());
        assertEquals("Java, Spring Boot, React", result.getRequiredSkills());
        assertEquals("New York, NY", result.getLocation());
        assertEquals(75000.0, result.getSalary());
        assertEquals(JobType.CONTRACT, result.getJobType());
        assertEquals(JobStatus.OPEN, result.getStatus());
        assertEquals(employer, result.getEmployer());
        assertEquals(jobDto.getStartDate(), result.getStartDate());
        assertEquals(jobDto.getEndDate(), result.getEndDate());

        verify(jobPostingRepository).save(any(JobPosting.class));
    }

    @Test
    void createJob_OneDayJob_ShouldSetCorrectDates() {
        // Given
        jobDto.setJobType(JobType.ONE_DAY);
        jobDto.setEndDate(null);
        
        when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employer));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        JobPosting result = jobService.createJob("employer@test.com", jobDto);

        // Then
        assertEquals(JobType.ONE_DAY, result.getJobType());
        assertEquals(jobDto.getStartDate(), result.getStartDate());
        assertNull(result.getEndDate());

        assertEquals(75000.0, result.getSalary());
        assertEquals("New York, NY", result.getLocation());
        assertEquals("Java, Spring Boot, React", result.getRequiredSkills());
    }
}
