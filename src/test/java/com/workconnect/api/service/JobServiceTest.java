package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.*;
import com.workconnect.api.dto.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.*;
import com.workconnect.api.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobService Tests")
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
    private User worker;
    private CreateJobRequestDto contractJobDto;
    private CreateJobRequestDto oneDayJobDto;
    private UpdateJobRequestDto updateJobDto;
    private JobPosting existingJob;
    private JobApplication jobApplication;

    @BeforeEach
    void setUp() {
        // Setup employer
        employer = new User();
        employer.setUserId(1L);
        employer.setEmail("employer@test.com");
        employer.setRole(Role.EMPLOYER);
        employer.setStatus(UserStatus.ACTIVE);

        // Setup worker
        worker = new User();
        worker.setUserId(2L);
        worker.setEmail("worker@test.com");
        worker.setRole(Role.WORKER);
        worker.setStatus(UserStatus.ACTIVE);

        // Setup contract job DTO
        contractJobDto = new CreateJobRequestDto();
        contractJobDto.setJobTitle("Software Engineer");
        contractJobDto.setDescription("Develop amazing software");
        contractJobDto.setRequiredSkills("Java, Spring Boot, React");
        contractJobDto.setLocation("New York, NY");
        contractJobDto.setSalary(75000.0);
        contractJobDto.setJobType(JobType.CONTRACT);
        contractJobDto.setStartDate(LocalDate.now().plusDays(7));
        contractJobDto.setEndDate(LocalDate.now().plusDays(37));

        // Setup one-day job DTO
        oneDayJobDto = new CreateJobRequestDto();
        oneDayJobDto.setJobTitle("Construction Helper");
        oneDayJobDto.setDescription("Help with construction work");
        oneDayJobDto.setRequiredSkills("Physical strength, Construction experience");
        oneDayJobDto.setLocation("Brooklyn, NY");
        oneDayJobDto.setSalary(200.0);
        oneDayJobDto.setJobType(JobType.ONE_DAY);
        oneDayJobDto.setStartDate(LocalDate.now().plusDays(3));

        // Setup update job DTO
        updateJobDto = new UpdateJobRequestDto();
        updateJobDto.setJobTitle("Senior Software Engineer");
        updateJobDto.setDescription("Lead software development");
        updateJobDto.setSalary(85000.0);

        // Setup existing job
        existingJob = new JobPosting();
        existingJob.setId(1L);
        existingJob.setJobTitle("Software Engineer");
        existingJob.setDescription("Develop amazing software");
        existingJob.setEmployer(employer);
        existingJob.setStatus(JobStatus.OPEN);
        existingJob.setJobType(JobType.CONTRACT);
        existingJob.setSalary(75000.0);
        existingJob.setLocation("New York, NY");
        existingJob.setRequiredSkills("Java, Spring Boot, React");

        // Setup job application
        jobApplication = new JobApplication();
        jobApplication.setId(1L);
        jobApplication.setWorker(worker);
        jobApplication.setJobPosting(existingJob);
        jobApplication.setStatus(JobApplicationStatus.PENDING);
        jobApplication.setAppliedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Job Creation Tests")
    class JobCreationTests {

        @Test
        @DisplayName("createJob_givenValidContractJobData_thenShouldCreateJobWithAllFields")
        void createJob_givenValidContractJobData_thenShouldCreateJobWithAllFields() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employer));
            when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
                JobPosting job = invocation.getArgument(0);
                job.setId(1L);
                return job;
            });

            // Act
            JobPosting result = jobService.createJob("employer@test.com", contractJobDto);

            // Assert
            assertNotNull(result);
            assertEquals("Software Engineer", result.getJobTitle());
            assertEquals("Develop amazing software", result.getDescription());
            assertEquals("Java, Spring Boot, React", result.getRequiredSkills());
            assertEquals("New York, NY", result.getLocation());
            assertEquals(75000.0, result.getSalary());
            assertEquals(JobType.CONTRACT, result.getJobType());
            assertEquals(JobStatus.OPEN, result.getStatus());
            assertEquals(employer, result.getEmployer());
            assertEquals(contractJobDto.getStartDate(), result.getStartDate());
            assertEquals(contractJobDto.getEndDate(), result.getEndDate());

            verify(jobPostingRepository).save(any(JobPosting.class));
        }

        @Test
        @DisplayName("createJob_givenValidOneDayJobData_thenShouldCreateJobWithCorrectDates")
        void createJob_givenValidOneDayJobData_thenShouldCreateJobWithCorrectDates() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employer));
            when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            JobPosting result = jobService.createJob("employer@test.com", oneDayJobDto);

            // Assert
            assertEquals(JobType.ONE_DAY, result.getJobType());
            assertEquals(oneDayJobDto.getStartDate(), result.getStartDate());
            assertNull(result.getEndDate());
            assertEquals(200.0, result.getSalary());
            assertEquals("Brooklyn, NY", result.getLocation());
            assertEquals("Physical strength, Construction experience", result.getRequiredSkills());
            assertEquals(JobStatus.OPEN, result.getStatus());
        }

        @Test
        @DisplayName("createJob_givenNonExistentEmployer_thenShouldThrowUsernameNotFoundException")
        void createJob_givenNonExistentEmployer_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () ->
                jobService.createJob("nonexistent@test.com", contractJobDto));

            verify(jobPostingRepository, never()).save(any(JobPosting.class));
        }

        @Test
        @DisplayName("createJob_givenNullJobData_thenShouldHandleGracefully")
        void createJob_givenNullJobData_thenShouldHandleGracefully() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employer));

            // Act & Assert
            assertThrows(NullPointerException.class, () ->
                jobService.createJob("employer@test.com", null));
        }
    }

    @Nested
    @DisplayName("Job Update Tests")
    class JobUpdateTests {

        @Test
        @DisplayName("updateJob_givenValidUpdateData_thenShouldUpdateJobSuccessfully")
        void updateJob_givenValidUpdateData_thenShouldUpdateJobSuccessfully() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            JobPosting result = jobService.updateJob("employer@test.com", 1L, updateJobDto);

            // Assert
            assertEquals("Senior Software Engineer", result.getJobTitle());
            assertEquals("Lead software development", result.getDescription());
            assertEquals(85000.0, result.getSalary());
            verify(jobPostingRepository).save(existingJob);
        }

        @Test
        @DisplayName("updateJob_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException")
        void updateJob_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));

            // Act & Assert
            assertThrows(AccessDeniedException.class, () ->
                jobService.updateJob("unauthorized@test.com", 1L, updateJobDto));

            verify(jobPostingRepository, never()).save(any(JobPosting.class));
        }

        @Test
        @DisplayName("updateJob_givenNonExistentJob_thenShouldThrowRuntimeException")
        void updateJob_givenNonExistentJob_thenShouldThrowRuntimeException() {
            // Arrange
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                jobService.updateJob("employer@test.com", 999L, updateJobDto));
        }
    }

    @Nested
    @DisplayName("Job Deletion Tests")
    class JobDeletionTests {

        @Test
        @DisplayName("deleteJob_givenJobWithNoApplications_thenShouldDeleteSuccessfully")
        void deleteJob_givenJobWithNoApplications_thenShouldDeleteSuccessfully() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobApplicationRepository.countByJobPosting_Id(1L)).thenReturn(0L);

            // Act
            assertDoesNotThrow(() -> jobService.deleteJob("employer@test.com", 1L));

            // Assert
            verify(jobPostingRepository).delete(existingJob);
        }

        @Test
        @DisplayName("deleteJob_givenJobWithApplications_thenShouldThrowIllegalStateException")
        void deleteJob_givenJobWithApplications_thenShouldThrowIllegalStateException() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobApplicationRepository.countByJobPosting_Id(1L)).thenReturn(3L);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                jobService.deleteJob("employer@test.com", 1L));

            assertTrue(exception.getMessage().contains("Cannot delete job with existing applications"));
            verify(jobPostingRepository, never()).delete(any(JobPosting.class));
        }

        @Test
        @DisplayName("deleteJob_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException")
        void deleteJob_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));

            // Act & Assert
            assertThrows(AccessDeniedException.class, () ->
                jobService.deleteJob("unauthorized@test.com", 1L));

            verify(jobPostingRepository, never()).delete(any(JobPosting.class));
        }
    }

    @Nested
    @DisplayName("Job Search and Retrieval Tests")
    class JobSearchTests {

        @Test
        @DisplayName("getAllOpenJobs_givenOpenJobs_thenShouldReturnJobListingDtos")
        void getAllOpenJobs_givenOpenJobs_thenShouldReturnJobListingDtos() {
            // Arrange
            List<JobPosting> openJobs = Arrays.asList(existingJob);
            when(jobPostingRepository.findByStatus(JobStatus.OPEN)).thenReturn(openJobs);

            // Act
            List<JobListingDto> result = jobService.getAllOpenJobs();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(jobPostingRepository).findByStatus(JobStatus.OPEN);
        }

        @Test
        @DisplayName("searchOpenJobs_givenKeyword_thenShouldReturnMatchingJobs")
        void searchOpenJobs_givenKeyword_thenShouldReturnMatchingJobs() {
            // Arrange
            String keyword = "Software";
            List<JobPosting> matchingJobs = Arrays.asList(existingJob);
            when(jobPostingRepository.findByStatusAndJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                JobStatus.OPEN, keyword, keyword)).thenReturn(matchingJobs);

            // Act
            List<JobListingDto> result = jobService.searchOpenJobs(keyword);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(jobPostingRepository).findByStatusAndJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                JobStatus.OPEN, keyword, keyword);
        }

        @Test
        @DisplayName("getJobById_givenNonExistentJobId_thenShouldThrowRuntimeException")
        void getJobById_givenNonExistentJobId_thenShouldThrowRuntimeException() {
            // Arrange
            when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> jobService.getJobById(999L));
        }
    }

    @Nested
    @DisplayName("Job Status Update Tests")
    class JobStatusUpdateTests {

        @Test
        @DisplayName("updateJobStatus_givenValidStatusUpdate_thenShouldUpdateSuccessfully")
        void updateJobStatus_givenValidStatusUpdate_thenShouldUpdateSuccessfully() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            JobPosting result = jobService.updateJobStatus("employer@test.com", 1L, JobStatus.CLOSED);

            // Assert
            assertEquals(JobStatus.CLOSED, result.getStatus());
            verify(jobPostingRepository).save(existingJob);
        }

        @Test
        @DisplayName("updateJobStatus_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException")
        void updateJobStatus_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));

            // Act & Assert
            assertThrows(AccessDeniedException.class, () ->
                jobService.updateJobStatus("unauthorized@test.com", 1L, JobStatus.CLOSED));
        }
    }

    @Nested
    @DisplayName("Job Application Tests")
    class JobApplicationTests {

        @Test
        @DisplayName("applyForJob_givenValidApplication_thenShouldCreateApplicationSuccessfully")
        void applyForJob_givenValidApplication_thenShouldCreateApplicationSuccessfully() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(worker));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobApplicationRepository.existsByWorker_UserIdAndJobPosting_Id(2L, 1L)).thenReturn(false);
            when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            assertDoesNotThrow(() -> jobService.applyForJob("worker@test.com", 1L));

            // Assert
            verify(jobApplicationRepository).save(any(JobApplication.class));
        }

        @Test
        @DisplayName("applyForJob_givenClosedJob_thenShouldThrowIllegalStateException")
        void applyForJob_givenClosedJob_thenShouldThrowIllegalStateException() {
            // Arrange
            existingJob.setStatus(JobStatus.CLOSED);
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(worker));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                jobService.applyForJob("worker@test.com", 1L));

            assertTrue(exception.getMessage().contains("no longer open for applications"));
            verify(jobApplicationRepository, never()).save(any(JobApplication.class));
        }

        @Test
        @DisplayName("applyForJob_givenDuplicateApplication_thenShouldThrowIllegalStateException")
        void applyForJob_givenDuplicateApplication_thenShouldThrowIllegalStateException() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(worker));
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobApplicationRepository.existsByWorker_UserIdAndJobPosting_Id(2L, 1L)).thenReturn(true);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                jobService.applyForJob("worker@test.com", 1L));

            assertTrue(exception.getMessage().contains("already applied for this job"));
            verify(jobApplicationRepository, never()).save(any(JobApplication.class));
        }

        @Test
        @DisplayName("getApplicationsForJob_givenValidEmployerAndJob_thenShouldReturnApplications")
        void getApplicationsForJob_givenValidEmployerAndJob_thenShouldReturnApplications() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));
            when(jobApplicationRepository.findByJobPosting_Id(1L)).thenReturn(Arrays.asList(jobApplication));

            // Act
            List<JobApplicationDto> result = jobService.getApplicationsForJob("employer@test.com", 1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(jobApplicationRepository).findByJobPosting_Id(1L);
        }

        @Test
        @DisplayName("getApplicationsForJob_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException")
        void getApplicationsForJob_givenUnauthorizedEmployer_thenShouldThrowAccessDeniedException() {
            // Arrange
            when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(existingJob));

            // Act & Assert
            assertThrows(AccessDeniedException.class, () ->
                jobService.getApplicationsForJob("unauthorized@test.com", 1L));
        }
    }
}
