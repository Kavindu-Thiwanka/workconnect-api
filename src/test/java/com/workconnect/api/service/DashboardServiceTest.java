package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.*;
import com.workconnect.api.dto.dashboard.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.*;
import com.workconnect.api.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService Tests")
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkerProfileRepository workerProfileRepository;

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User workerUser;
    private User employerUser;
    private WorkerProfile workerProfile;
    private EmployerProfile employerProfile;
    private JobPosting jobPosting1;
    private JobPosting jobPosting2;
    private JobApplication jobApplication1;
    private JobApplication jobApplication2;
    private Skill skill1;
    private Skill skill2;

    @BeforeEach
    void setUp() {
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
        workerProfile.setPhoneNumber("123-456-7890");
        workerProfile.setLocation("New York, NY");
        workerProfile.setBio("Experienced developer");
        workerProfile.setSkills(Set.of(skill1, skill2));
        workerProfile.setExperience("5 years");
        workerProfile.setEducation("Bachelor's in CS");
        workerProfile.setProfileImageUrl("http://example.com/profile.jpg");
        workerProfile.setResumeUrl("http://example.com/resume.jpg");
        workerUser.setProfile(workerProfile);

        // Setup employer user and profile
        employerUser = new User();
        employerUser.setUserId(2L);
        employerUser.setEmail("employer@test.com");
        employerUser.setRole(Role.EMPLOYER);
        employerUser.setStatus(UserStatus.ACTIVE);

        employerProfile = new EmployerProfile();
        employerProfile.setId(1L);
        employerProfile.setUser(employerUser);
        employerProfile.setCompanyName("Tech Corp");
        employerProfile.setCompanyDescription("Leading tech company");
        employerProfile.setLocation("San Francisco, CA");
        employerProfile.setCompanyLogoUrl("http://example.com/logo.jpg");
        employerUser.setProfile(employerProfile);

        // Setup job postings
        jobPosting1 = new JobPosting();
        jobPosting1.setId(1L);
        jobPosting1.setJobTitle("Java Developer");
        jobPosting1.setDescription("Java development position");
        jobPosting1.setRequiredSkills("Java Spring Boot");
        jobPosting1.setEmployer(employerUser);
        jobPosting1.setStatus(JobStatus.OPEN);
        jobPosting1.setLocation("New York, NY");
        jobPosting1.setSalary(75000.0);

        jobPosting2 = new JobPosting();
        jobPosting2.setId(2L);
        jobPosting2.setJobTitle("Senior Java Developer");
        jobPosting2.setDescription("Senior Java development position");
        jobPosting2.setRequiredSkills("Java Spring Boot Microservices");
        jobPosting2.setEmployer(employerUser);
        jobPosting2.setStatus(JobStatus.CLOSED);
        jobPosting2.setLocation("Boston, MA");
        jobPosting2.setSalary(95000.0);

        // Setup job applications
        jobApplication1 = new JobApplication();
        jobApplication1.setId(1L);
        jobApplication1.setWorker(workerUser);
        jobApplication1.setJobPosting(jobPosting1);
        jobApplication1.setStatus(JobApplicationStatus.PENDING);
        jobApplication1.setAppliedAt(LocalDateTime.now().minusDays(1));

        jobApplication2 = new JobApplication();
        jobApplication2.setId(2L);
        jobApplication2.setWorker(workerUser);
        jobApplication2.setJobPosting(jobPosting2);
        jobApplication2.setStatus(JobApplicationStatus.COMPLETED);
        jobApplication2.setAppliedAt(LocalDateTime.now().minusDays(3));
    }

    @Nested
    @DisplayName("Worker Dashboard Tests")
    class WorkerDashboardTests {

        @Test
        @DisplayName("getWorkerDashboard_givenNonExistentWorker_thenShouldThrowUsernameNotFoundException")
        void getWorkerDashboard_givenNonExistentWorker_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () -> 
                dashboardService.getWorkerDashboard("nonexistent@test.com"));
        }

        @Test
        @DisplayName("getWorkerDashboard_givenWorkerWithoutProfile_thenShouldThrowIllegalStateException")
        void getWorkerDashboard_givenWorkerWithoutProfile_thenShouldThrowIllegalStateException() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(workerProfileRepository.findByUser(workerUser)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> 
                dashboardService.getWorkerDashboard("worker@test.com"));
        }
    }

    @Nested
    @DisplayName("Employer Dashboard Tests")
    class EmployerDashboardTests {

        @Test
        @DisplayName("getEmployerDashboard_givenNonExistentEmployer_thenShouldThrowUsernameNotFoundException")
        void getEmployerDashboard_givenNonExistentEmployer_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () -> 
                dashboardService.getEmployerDashboard("nonexistent@test.com"));
        }

        @Test
        @DisplayName("getEmployerDashboard_givenEmployerWithoutProfile_thenShouldThrowIllegalStateException")
        void getEmployerDashboard_givenEmployerWithoutProfile_thenShouldThrowIllegalStateException() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));
            when(employerProfileRepository.findByUser(employerUser)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> 
                dashboardService.getEmployerDashboard("employer@test.com"));
        }
    }

    @Nested
    @DisplayName("Worker Recommendations Tests")
    class WorkerRecommendationsTests {

        @Test
        @DisplayName("getWorkerRecommendations_givenValidWorkerWithLocation_thenShouldReturnFilteredRecommendations")
        void getWorkerRecommendations_givenValidWorkerWithLocation_thenShouldReturnFilteredRecommendations() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(jobPosting1); // jobPosting1 has matching location
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(workerProfileRepository.findByUser(workerUser)).thenReturn(Optional.of(workerProfile));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);

            // Act
            JobRecommendationsDto result = dashboardService.getWorkerRecommendations("worker@test.com", 5);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getRecommendations());
            assertEquals(1, result.getTotalCount());
            assertTrue(result.getRecommendationReason().contains("location"));
        }

        @Test
        @DisplayName("getWorkerRecommendations_givenWorkerWithNoLocation_thenShouldReturnAllOpenJobs")
        void getWorkerRecommendations_givenWorkerWithNoLocation_thenShouldReturnAllOpenJobs() {
            // Arrange
            workerProfile.setLocation(null);
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);
            
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(workerProfileRepository.findByUser(workerUser)).thenReturn(Optional.of(workerProfile));
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);

            // Act
            JobRecommendationsDto result = dashboardService.getWorkerRecommendations("worker@test.com", 5);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalCount());
        }
    }

    @Nested
    @DisplayName("Profile Completion Tests")
    class ProfileCompletionTests {

        @Test
        @DisplayName("getWorkerProfileCompletion_givenCompleteProfile_thenShouldReturn100Percent")
        void getWorkerProfileCompletion_givenCompleteProfile_thenShouldReturn100Percent() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(workerProfileRepository.findByUser(workerUser)).thenReturn(Optional.of(workerProfile));

            // Act
            ProfileCompletionDto result = dashboardService.getWorkerProfileCompletion("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getPercentage());
            assertTrue(result.getTips().isEmpty()); // No tips needed for complete profile
        }

        @Test
        @DisplayName("getWorkerProfileCompletion_givenIncompleteProfile_thenShouldReturnLowerPercentageWithTips")
        void getWorkerProfileCompletion_givenIncompleteProfile_thenShouldReturnLowerPercentageWithTips() {
            // Arrange
            workerProfile.setPhoneNumber(null);
            workerProfile.setBio(null);
            workerProfile.setProfileImageUrl(null);
            workerProfile.setSkills(null);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(workerProfileRepository.findByUser(workerUser)).thenReturn(Optional.of(workerProfile));

            // Act
            ProfileCompletionDto result = dashboardService.getWorkerProfileCompletion("worker@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result.getPercentage() < 100);
            assertFalse(result.getTips().isEmpty());

            // Check that tips contain suggestions for missing fields
            List<String> tips = result.getTips();
            assertTrue(tips.stream().anyMatch(tip -> tip.contains("phone number")));
            assertTrue(tips.stream().anyMatch(tip -> tip.contains("bio")));
            assertTrue(tips.stream().anyMatch(tip -> tip.contains("profile picture")));
            assertTrue(tips.stream().anyMatch(tip -> tip.contains("skills")));
        }

        @Test
        @DisplayName("getWorkerProfileCompletion_givenPartiallyCompleteProfile_thenShouldCalculateCorrectPercentage")
        void getWorkerProfileCompletion_givenPartiallyCompleteProfile_thenShouldCalculateCorrectPercentage() {
            workerProfile.setPhoneNumber(null);
            workerProfile.setBio(null);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(workerProfileRepository.findByUser(workerUser)).thenReturn(Optional.of(workerProfile));

            // Act
            ProfileCompletionDto result = dashboardService.getWorkerProfileCompletion("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(80, result.getPercentage());
            assertEquals(2, result.getTips().size());
        }
    }

    @Nested
    @DisplayName("Statistics Calculation Tests")
    class StatisticsTests {

        @Test
        @DisplayName("getWorkerStats_givenVariousApplicationStatuses_thenShouldCalculateCorrectStats")
        void getWorkerStats_givenVariousApplicationStatuses_thenShouldCalculateCorrectStats() {
            // Arrange
            List<JobApplication> applications = Arrays.asList(jobApplication1, jobApplication2);
            List<JobPosting> openJobs = Arrays.asList(jobPosting1);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(jobApplicationRepository.findByWorker_Email("worker@test.com")).thenReturn(applications);
            when(jobApplicationRepository.countByWorkerAndStatus(workerUser, JobApplicationStatus.PENDING)).thenReturn(1L);
            when(jobApplicationRepository.countByWorkerAndStatus(workerUser, JobApplicationStatus.COMPLETED)).thenReturn(1L);
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);

            // Act
            WorkerStatsDto result = dashboardService.getWorkerStats("worker@test.com");

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getTotalApplications());
            assertEquals(1, result.getPendingApplications());
            assertEquals(1, result.getInterviewsScheduled());
            assertEquals(1, result.getJobMatchesThisWeek());
            assertEquals(0, result.getProfileViews()); // Default implementation
        }
    }
}
