package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.Role;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.entity.*;
import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.repository.*;
import com.workconnect.api.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceIntegrationTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobService jobService;

    private RecommendationServiceImpl recommendationService;

    private User testWorker;
    private WorkerProfile testWorkerProfile;
    private List<JobPosting> testJobs;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationServiceImpl(
                restTemplate, userRepository, jobPostingRepository, jobService);

        // Create test worker
        testWorker = new User();
        testWorker.setUserId(1L);
        testWorker.setEmail("worker@test.com");
        testWorker.setRole(Role.WORKER);

        // Create test worker profile with skills
        testWorkerProfile = new WorkerProfile();
        testWorkerProfile.setId(1L);
        testWorkerProfile.setUser(testWorker);
        
        Set<Skill> skills = new HashSet<>();
        Skill skill1 = new Skill();
        skill1.setName("Java");
        Skill skill2 = new Skill();
        skill2.setName("Spring Boot");
        skills.add(skill1);
        skills.add(skill2);
        testWorkerProfile.setSkills(skills);
        
        testWorker.setProfile(testWorkerProfile);

        // Create test jobs
        testJobs = Arrays.asList(
                createTestJob(1L, "Java Developer", "Java Spring Boot REST API"),
                createTestJob(2L, "Python Developer", "Python Django Machine Learning"),
                createTestJob(3L, "Frontend Developer", "React JavaScript TypeScript")
        );
    }

    @Test
    void testGetJobRecommendations_NoSkills_FallbackRecommendations() {
        // Arrange
        testWorkerProfile.setSkills(new HashSet<>()); // No skills
        when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(testWorker));
        when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(testJobs);
        when(jobService.mapToJobListingDto(any(JobPosting.class))).thenReturn(createTestJobDto());

        // Act
        List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    void testGetJobRecommendations_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> 
                recommendationService.getJobRecommendations("nonexistent@test.com"));
    }

    @Test
    void testGetJobRecommendations_NotWorker_ReturnsEmpty() {
        // Arrange
        User employer = new User();
        employer.setRole(Role.EMPLOYER);
        employer.setProfile(new EmployerProfile());
        
        when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employer));

        // Act
        List<JobListingDto> result = recommendationService.getJobRecommendations("employer@test.com");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private JobPosting createTestJob(Long id, String title, String requiredSkills) {
        JobPosting job = new JobPosting();
        job.setId(id);
        job.setJobTitle(title);
        job.setRequiredSkills(requiredSkills);
        job.setStatus(JobStatus.OPEN);
        job.setPostedAt(LocalDateTime.now());
        return job;
    }

    private JobListingDto createTestJobDto() {
        return JobListingDto.builder()
                .id(1L)
                .jobTitle("Test Job")
                .description("Test Description")
                .location("Test Location")
                .requiredSkills("Test Skills")
                .employerCompanyName("Test Company")
                .postedAt(LocalDateTime.now())
                .build();
    }
}
