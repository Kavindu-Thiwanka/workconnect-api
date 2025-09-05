package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.*;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.ai.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.JobPostingRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService Tests")
class RecommendationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobService jobService;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private User workerUser;
    private User employerUser;
    private WorkerProfile workerProfile;
    private JobPosting jobPosting1;
    private JobPosting jobPosting2;
    private Skill skill1;
    private Skill skill2;
    private JobListingDto jobListingDto1;
    private JobListingDto jobListingDto2;

    @BeforeEach
    void setUp() {
        // Set AI service URL using reflection
        ReflectionTestUtils.setField(recommendationService, "aiServiceUrl", "http://localhost:8000/recommendations/jobs");

        // Setup skills
        skill1 = new Skill("Java");
        skill1.setId(1L);
        skill2 = new Skill("Spring Boot");
        skill2.setId(2L);

        // Setup worker user and profile
        workerUser = new User();
        workerUser.setUserId(1L);
        workerUser.setEmail("worker@test.com");
        workerUser.setRole(Role.WORKER);
        workerUser.setStatus(UserStatus.ACTIVE);

        workerProfile = new WorkerProfile();
        workerProfile.setId(1L);
        workerProfile.setUser(workerUser);
        workerProfile.setFirstName("John");
        workerProfile.setLastName("Doe");
        workerProfile.setSkills(Set.of(skill1, skill2));
        workerUser.setProfile(workerProfile);

        // Setup employer user
        employerUser = new User();
        employerUser.setUserId(2L);
        employerUser.setEmail("employer@test.com");
        employerUser.setRole(Role.EMPLOYER);
        employerUser.setStatus(UserStatus.ACTIVE);

        // Setup job postings
        jobPosting1 = new JobPosting();
        jobPosting1.setId(1L);
        jobPosting1.setJobTitle("Java Developer");
        jobPosting1.setDescription("Java development position");
        jobPosting1.setRequiredSkills("Java Spring Boot");
        jobPosting1.setEmployer(employerUser);
        jobPosting1.setStatus(JobStatus.OPEN);

        jobPosting2 = new JobPosting();
        jobPosting2.setId(2L);
        jobPosting2.setJobTitle("Frontend Developer");
        jobPosting2.setDescription("React development position");
        jobPosting2.setRequiredSkills("React JavaScript");
        jobPosting2.setEmployer(employerUser);
        jobPosting2.setStatus(JobStatus.OPEN);

        // Setup job listing DTOs
        jobListingDto1 = new JobListingDto();
        jobListingDto1.setId(1L);
        jobListingDto1.setJobTitle("Java Developer");
        jobListingDto1.setDescription("Java development position");

        jobListingDto2 = new JobListingDto();
        jobListingDto2.setId(2L);
        jobListingDto2.setJobTitle("Frontend Developer");
        jobListingDto2.setDescription("React development position");
    }

    @Nested
    @DisplayName("Job Recommendations Tests")
    class JobRecommendationsTests {

        @Test
        @DisplayName("getJobRecommendations_givenValidWorkerWithSkills_thenShouldReturnRankedRecommendations")
        void getJobRecommendations_givenValidWorkerWithSkills_thenShouldReturnRankedRecommendations() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1, jobPosting2);
            List<Long> rankedJobIds = Arrays.asList(1L, 2L);
            
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(rankedJobIds);
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);
            when(jobPostingRepository.findAllById(rankedJobIds)).thenReturn(Arrays.asList(jobPosting1, jobPosting2));
            when(jobService.mapToJobListingDto(jobPosting1)).thenReturn(jobListingDto1);
            when(jobService.mapToJobListingDto(jobPosting2)).thenReturn(jobListingDto2);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Java Developer", result.get(0).getJobTitle());
            assertEquals("Frontend Developer", result.get(1).getJobTitle());
            
            verify(restTemplate).postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class));
            verify(jobService, times(2)).mapToJobListingDto(any(JobPosting.class));
        }

        @Test
        @DisplayName("getJobRecommendations_givenWorkerWithNoSkills_thenShouldCallAiServiceWithEmptySkills")
        void getJobRecommendations_givenWorkerWithNoSkills_thenShouldCallAiServiceWithEmptySkills() {
            // Arrange
            workerProfile.setSkills(new HashSet<>());
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(Arrays.asList(1L));
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);
            when(jobPostingRepository.findAllById(anyList())).thenReturn(Arrays.asList(jobPosting1));
            when(jobService.mapToJobListingDto(any(JobPosting.class))).thenReturn(jobListingDto1);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            
            // Verify that AI service was called with empty skills string
            verify(restTemplate).postForObject(anyString(), argThat(request -> {
                AiRecommendationRequestDto req = (AiRecommendationRequestDto) request;
                return req.worker_profile().skills().isEmpty();
            }), eq(AiRecommendationResponseDto.class));
        }

        @Test
        @DisplayName("getJobRecommendations_givenNonExistentWorker_thenShouldThrowUsernameNotFoundException")
        void getJobRecommendations_givenNonExistentWorker_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () -> 
                recommendationService.getJobRecommendations("nonexistent@test.com"));
            
            verify(restTemplate, never()).postForObject(anyString(), any(), any());
        }

        @Test
        @DisplayName("getJobRecommendations_givenEmployerUser_thenShouldReturnEmptyList")
        void getJobRecommendations_givenEmployerUser_thenShouldReturnEmptyList() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("employer@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            
            verify(restTemplate, never()).postForObject(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("AI Service Integration Tests")
    class AiServiceIntegrationTests {

        @Test
        @DisplayName("getJobRecommendations_givenAiServiceReturnsNull_thenShouldReturnEmptyList")
        void getJobRecommendations_givenAiServiceReturnsNull_thenShouldReturnEmptyList() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(null);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getJobRecommendations_givenAiServiceReturnsEmptyJobIds_thenShouldReturnEmptyList")
        void getJobRecommendations_givenAiServiceReturnsEmptyJobIds_thenShouldReturnEmptyList() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(Collections.emptyList());
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getJobRecommendations_givenAiServiceThrowsException_thenShouldPropagateException")
        void getJobRecommendations_givenAiServiceThrowsException_thenShouldPropagateException() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenThrow(new RestClientException("AI service unavailable"));

            // Act & Assert
            assertThrows(RestClientException.class, () -> 
                recommendationService.getJobRecommendations("worker@test.com"));
        }

        @Test
        @DisplayName("getJobRecommendations_givenAiServiceReturnsNullJobIds_thenShouldReturnEmptyList")
        void getJobRecommendations_givenAiServiceReturnsNullJobIds_thenShouldReturnEmptyList() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(null);
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Data Consistency and Edge Cases Tests")
    class DataConsistencyTests {

        @Test
        @DisplayName("getJobRecommendations_givenJobsNotFoundInDatabase_thenShouldFilterOutMissingJobs")
        void getJobRecommendations_givenJobsNotFoundInDatabase_thenShouldFilterOutMissingJobs() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1, jobPosting2);
            List<Long> rankedJobIds = Arrays.asList(1L, 2L, 999L); // 999L doesn't exist
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(rankedJobIds);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);
            when(jobPostingRepository.findAllById(rankedJobIds)).thenReturn(Arrays.asList(jobPosting1, jobPosting2));
            when(jobService.mapToJobListingDto(jobPosting1)).thenReturn(jobListingDto1);
            when(jobService.mapToJobListingDto(jobPosting2)).thenReturn(jobListingDto2);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size()); // Should filter out the missing job (999L)
            assertEquals("Java Developer", result.get(0).getJobTitle());
            assertEquals("Frontend Developer", result.get(1).getJobTitle());
        }

        @Test
        @DisplayName("getJobRecommendations_givenNoOpenJobs_thenShouldReturnEmptyList")
        void getJobRecommendations_givenNoOpenJobs_thenShouldReturnEmptyList() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(Collections.emptyList());

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verify AI service is still called but with empty job list
            verify(restTemplate).postForObject(anyString(), argThat(request -> {
                AiRecommendationRequestDto req = (AiRecommendationRequestDto) request;
                return req.job_postings().isEmpty();
            }), eq(AiRecommendationResponseDto.class));
        }

        @Test
        @DisplayName("getJobRecommendations_givenJobsWithNullRequiredSkills_thenShouldHandleGracefully")
        void getJobRecommendations_givenJobsWithNullRequiredSkills_thenShouldHandleGracefully() {
            // Arrange
            jobPosting1.setRequiredSkills(null);
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(Arrays.asList(1L));

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);
            when(jobPostingRepository.findAllById(anyList())).thenReturn(Arrays.asList(jobPosting1));
            when(jobService.mapToJobListingDto(any(JobPosting.class))).thenReturn(jobListingDto1);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());

            // Verify AI service was called with empty string for null required skills
            verify(restTemplate).postForObject(anyString(), argThat(request -> {
                AiRecommendationRequestDto req = (AiRecommendationRequestDto) request;
                return req.job_postings().get(0).required_skills().isEmpty();
            }), eq(AiRecommendationResponseDto.class));
        }

        @Test
        @DisplayName("getJobRecommendations_givenWorkerWithNullSkills_thenShouldHandleGracefully")
        void getJobRecommendations_givenWorkerWithNullSkills_thenShouldHandleGracefully() {
            // Arrange
            workerProfile.setSkills(null);
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(Arrays.asList(1L));

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);
            when(jobPostingRepository.findAllById(anyList())).thenReturn(Arrays.asList(jobPosting1));
            when(jobService.mapToJobListingDto(any(JobPosting.class))).thenReturn(jobListingDto1);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());

            // Verify AI service was called with empty skills string
            verify(restTemplate).postForObject(anyString(), argThat(request -> {
                AiRecommendationRequestDto req = (AiRecommendationRequestDto) request;
                return req.worker_profile().skills().isEmpty();
            }), eq(AiRecommendationResponseDto.class));
        }

        @Test
        @DisplayName("getJobRecommendations_givenCorrectOrderPreservation_thenShouldMaintainAiRanking")
        void getJobRecommendations_givenCorrectOrderPreservation_thenShouldMaintainAiRanking() {
            // Arrange - AI returns jobs in specific order (2L, 1L)
            List<JobPosting> openJobs = Arrays.asList(jobPosting1, jobPosting2);
            List<Long> rankedJobIds = Arrays.asList(2L, 1L); // Reverse order from database
            AiRecommendationResponseDto aiResponse = new AiRecommendationResponseDto(rankedJobIds);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);
            when(restTemplate.postForObject(anyString(), any(AiRecommendationRequestDto.class), eq(AiRecommendationResponseDto.class)))
                .thenReturn(aiResponse);
            when(jobPostingRepository.findAllById(rankedJobIds)).thenReturn(Arrays.asList(jobPosting1, jobPosting2));
            when(jobService.mapToJobListingDto(jobPosting1)).thenReturn(jobListingDto1);
            when(jobService.mapToJobListingDto(jobPosting2)).thenReturn(jobListingDto2);

            // Act
            List<JobListingDto> result = recommendationService.getJobRecommendations("worker@test.com");

            // Assert - Should maintain AI's ranking order (2L first, then 1L)
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Frontend Developer", result.get(0).getJobTitle()); // Job 2L first
            assertEquals("Java Developer", result.get(1).getJobTitle()); // Job 1L second
        }
    }
}
