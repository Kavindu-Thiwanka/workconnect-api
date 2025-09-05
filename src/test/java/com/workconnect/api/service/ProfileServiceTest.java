package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.*;
import com.workconnect.api.dto.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.SkillRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Tests")
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User workerUser;
    private User employerUser;
    private WorkerProfile workerProfile;
    private EmployerProfile employerProfile;
    private WorkerProfileDto workerProfileDto;
    private EmployerProfileDto employerProfileDto;
    private Skill skill1;
    private Skill skill2;
    private Skill newSkill;

    @BeforeEach
    void setUp() {
        // Setup skills
        skill1 = new Skill("Java");
        skill1.setId(1L);
        skill2 = new Skill("Spring Boot");
        skill2.setId(2L);
        newSkill = new Skill("React");
        newSkill.setId(3L);

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
        workerProfile.setSkills(new HashSet<>(Arrays.asList(skill1, skill2)));
        workerProfile.setExperience("5 years");
        workerProfile.setEducation("Bachelor's in CS");
        workerProfile.setProfileImageUrl("http://example.com/profile.jpg");
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

        // Setup DTOs
        workerProfileDto = new WorkerProfileDto();
        workerProfileDto.setFirstName("Jane");
        workerProfileDto.setLastName("Smith");
        workerProfileDto.setPhoneNumber("987-654-3210");
        workerProfileDto.setLocation("Boston, MA");
        workerProfileDto.setBio("Senior developer");
        workerProfileDto.setSkills((Set<String>) Arrays.asList("Java", "Spring Boot", "React"));
        workerProfileDto.setExperience("7 years");
        workerProfileDto.setEducation("Master's in CS");

        employerProfileDto = new EmployerProfileDto();
        employerProfileDto.setCompanyName("Startup Inc");
        employerProfileDto.setCompanyDescription("Innovative startup");
        employerProfileDto.setLocation("Austin, TX");
    }

    @Nested
    @DisplayName("Profile Retrieval Tests")
    class ProfileRetrievalTests {

        @Test
        @DisplayName("getProfile_givenWorkerUser_thenShouldReturnWorkerProfileDto")
        void getProfile_givenWorkerUser_thenShouldReturnWorkerProfileDto() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));

            // Act
            Object result = profileService.getProfile("worker@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result instanceof WorkerProfileDto);
            WorkerProfileDto dto = (WorkerProfileDto) result;
            assertEquals("John", dto.getFirstName());
            assertEquals("Doe", dto.getLastName());
            assertEquals("123-456-7890", dto.getPhoneNumber());
            assertEquals("New York, NY", dto.getLocation());
            assertEquals("Experienced developer", dto.getBio());
            assertEquals(2, dto.getSkills().size());
            assertTrue(dto.getSkills().contains("Java"));
            assertTrue(dto.getSkills().contains("Spring Boot"));
        }

        @Test
        @DisplayName("getProfile_givenEmployerUser_thenShouldReturnEmployerProfileDto")
        void getProfile_givenEmployerUser_thenShouldReturnEmployerProfileDto() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));

            // Act
            Object result = profileService.getProfile("employer@test.com");

            // Assert
            assertNotNull(result);
            assertTrue(result instanceof EmployerProfileDto);
            EmployerProfileDto dto = (EmployerProfileDto) result;
            assertEquals("Tech Corp", dto.getCompanyName());
            assertEquals("Leading tech company", dto.getCompanyDescription());
            assertEquals("San Francisco, CA", dto.getLocation());
        }

        @Test
        @DisplayName("getProfile_givenNonExistentUser_thenShouldThrowUsernameNotFoundException")
        void getProfile_givenNonExistentUser_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () -> 
                profileService.getProfile("nonexistent@test.com"));
        }

        @Test
        @DisplayName("getProfile_givenUserWithNullProfile_thenShouldReturnNull")
        void getProfile_givenUserWithNullProfile_thenShouldReturnNull() {
            // Arrange
            workerUser.setProfile(null);
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));

            // Act
            Object result = profileService.getProfile("worker@test.com");

            // Assert
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Worker Profile Update Tests")
    class WorkerProfileUpdateTests {

        @Test
        @DisplayName("updateWorkerProfile_givenValidUpdate_thenShouldUpdateAllFields")
        void updateWorkerProfile_givenValidUpdate_thenShouldUpdateAllFields() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.of(skill1));
            when(skillRepository.findByNameIgnoreCase("Spring Boot")).thenReturn(Optional.of(skill2));
            when(skillRepository.findByNameIgnoreCase("React")).thenReturn(Optional.empty());
            when(skillRepository.save(any(Skill.class))).thenReturn(newSkill);
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            assertEquals("Jane", result.getFirstName());
            assertEquals("Smith", result.getLastName());
            assertEquals("987-654-3210", result.getPhoneNumber());
            assertEquals("Boston, MA", result.getLocation());
            assertEquals("Senior developer", result.getBio());
            assertEquals("7 years", result.getExperience());
            assertEquals("Master's in CS", result.getEducation());
            assertEquals(3, result.getSkills().size());
            
            // Verify profile was updated
            assertEquals("Jane", workerProfile.getFirstName());
            assertEquals("Smith", workerProfile.getLastName());
            assertEquals(3, workerProfile.getSkills().size());
            
            verify(userRepository).save(workerUser);
            verify(skillRepository).save(any(Skill.class)); // New skill "React" was created
        }

        @Test
        @DisplayName("updateWorkerProfile_givenNonWorkerUser_thenShouldThrowIllegalStateException")
        void updateWorkerProfile_givenNonWorkerUser_thenShouldThrowIllegalStateException() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                profileService.updateWorkerProfile("employer@test.com", workerProfileDto));
            
            assertTrue(exception.getMessage().contains("not a worker"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("updateWorkerProfile_givenNullSkills_thenShouldHandleGracefully")
        void updateWorkerProfile_givenNullSkills_thenShouldHandleGracefully() {
            // Arrange
            workerProfileDto.setSkills(null);
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            assertEquals("Jane", result.getFirstName());
            // Skills should remain unchanged when null is passed
            assertEquals(2, result.getSkills().size());
            
            verify(skillRepository, never()).findByNameIgnoreCase(anyString());
            verify(skillRepository, never()).save(any(Skill.class));
        }

        @Test
        @DisplayName("updateWorkerProfile_givenEmptySkills_thenShouldClearSkills")
        void updateWorkerProfile_givenEmptySkills_thenShouldClearSkills() {
            // Arrange
            workerProfileDto.setSkills(new HashSet<>());
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            assertTrue(result.getSkills().isEmpty());
            assertTrue(workerProfile.getSkills().isEmpty());
        }
    }

    @Nested
    @DisplayName("Employer Profile Update Tests")
    class EmployerProfileUpdateTests {

        @Test
        @DisplayName("updateEmployerProfile_givenValidUpdate_thenShouldUpdateAllFields")
        void updateEmployerProfile_givenValidUpdate_thenShouldUpdateAllFields() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));
            when(userRepository.save(any(User.class))).thenReturn(employerUser);

            // Act
            EmployerProfileDto result = profileService.updateEmployerProfile("employer@test.com", employerProfileDto);

            // Assert
            assertNotNull(result);
            assertEquals("Startup Inc", result.getCompanyName());
            assertEquals("Innovative startup", result.getCompanyDescription());
            assertEquals("Austin, TX", result.getLocation());
            
            // Verify profile was updated
            assertEquals("Startup Inc", employerProfile.getCompanyName());
            assertEquals("Innovative startup", employerProfile.getCompanyDescription());
            assertEquals("Austin, TX", employerProfile.getLocation());
            
            verify(userRepository).save(employerUser);
        }

        @Test
        @DisplayName("updateEmployerProfile_givenNonEmployerUser_thenShouldThrowIllegalStateException")
        void updateEmployerProfile_givenNonEmployerUser_thenShouldThrowIllegalStateException() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                profileService.updateEmployerProfile("worker@test.com", employerProfileDto));
            
            assertTrue(exception.getMessage().contains("not an employer"));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Profile Picture Update Tests")
    class ProfilePictureUpdateTests {

        @Test
        @DisplayName("updateProfilePicture_givenWorkerUser_thenShouldUpdateWorkerProfileImage")
        void updateProfilePicture_givenWorkerUser_thenShouldUpdateWorkerProfileImage() {
            // Arrange
            String newImageUrl = "http://example.com/new-profile.jpg";
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            profileService.updateProfilePicture("worker@test.com", newImageUrl);

            // Assert
            assertEquals(newImageUrl, workerProfile.getProfileImageUrl());
            verify(userRepository).save(workerUser);
        }

        @Test
        @DisplayName("updateProfilePicture_givenEmployerUser_thenShouldUpdateEmployerLogo")
        void updateProfilePicture_givenEmployerUser_thenShouldUpdateEmployerLogo() {
            // Arrange
            String newLogoUrl = "http://example.com/new-logo.jpg";
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));
            when(userRepository.save(any(User.class))).thenReturn(employerUser);

            // Act
            profileService.updateProfilePicture("employer@test.com", newLogoUrl);

            // Assert
            assertEquals(newLogoUrl, employerProfile.getCompanyLogoUrl());
            verify(userRepository).save(employerUser);
        }

        @Test
        @DisplayName("updateProfilePicture_givenNonExistentUser_thenShouldThrowUsernameNotFoundException")
        void updateProfilePicture_givenNonExistentUser_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () -> 
                profileService.updateProfilePicture("nonexistent@test.com", "http://example.com/image.jpg"));
            
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("updateProfilePicture_givenNullImageUrl_thenShouldSetNullValue")
        void updateProfilePicture_givenNullImageUrl_thenShouldSetNullValue() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            profileService.updateProfilePicture("worker@test.com", null);

            // Assert
            assertNull(workerProfile.getProfileImageUrl());
            verify(userRepository).save(workerUser);
        }
    }

    @Nested
    @DisplayName("Skill Management Tests")
    class SkillManagementTests {

        @Test
        @DisplayName("updateWorkerProfile_givenMixOfExistingAndNewSkills_thenShouldHandleBothCorrectly")
        void updateWorkerProfile_givenMixOfExistingAndNewSkills_thenShouldHandleBothCorrectly() {
            // Arrange
            Set<String> skillNames = (Set<String>) Arrays.asList("Java", "Python", "Docker"); // Java exists, Python and Docker are new
            workerProfileDto.setSkills(skillNames);

            Skill pythonSkill = new Skill("Python");
            pythonSkill.setId(4L);
            Skill dockerSkill = new Skill("Docker");
            dockerSkill.setId(5L);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.of(skill1));
            when(skillRepository.findByNameIgnoreCase("Python")).thenReturn(Optional.empty());
            when(skillRepository.findByNameIgnoreCase("Docker")).thenReturn(Optional.empty());
            when(skillRepository.save(argThat(skill -> "Python".equals(skill.getName())))).thenReturn(pythonSkill);
            when(skillRepository.save(argThat(skill -> "Docker".equals(skill.getName())))).thenReturn(dockerSkill);
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.getSkills().size());
            assertTrue(result.getSkills().contains("Java"));
            assertTrue(result.getSkills().contains("Python"));
            assertTrue(result.getSkills().contains("Docker"));

            // Verify that new skills were created
            verify(skillRepository, times(2)).save(any(Skill.class));
            verify(skillRepository).save(argThat(skill -> "Python".equals(skill.getName())));
            verify(skillRepository).save(argThat(skill -> "Docker".equals(skill.getName())));
        }

        @Test
        @DisplayName("updateWorkerProfile_givenDuplicateSkillNames_thenShouldHandleGracefully")
        void updateWorkerProfile_givenDuplicateSkillNames_thenShouldHandleGracefully() {
            // Arrange
            List<String> skillNames = Arrays.asList("Java", "java", "JAVA"); // Same skill with different cases
            workerProfileDto.setSkills((Set<String>) skillNames);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.of(skill1));
            when(skillRepository.findByNameIgnoreCase("java")).thenReturn(Optional.of(skill1));
            when(skillRepository.findByNameIgnoreCase("JAVA")).thenReturn(Optional.of(skill1));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            // Should only have one Java skill despite duplicates
            assertEquals(1, result.getSkills().size());
            assertTrue(result.getSkills().contains("Java"));

            // Verify no new skills were created
            verify(skillRepository, never()).save(any(Skill.class));
        }

        @Test
        @DisplayName("updateWorkerProfile_givenSkillsWithWhitespace_thenShouldTrimAndHandle")
        void updateWorkerProfile_givenSkillsWithWhitespace_thenShouldTrimAndHandle() {
            // Arrange
            List<String> skillNames = Arrays.asList("  Java  ", " Spring Boot ", "React   ");
            workerProfileDto.setSkills((Set<String>) skillNames);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.of(skill1));
            when(skillRepository.findByNameIgnoreCase("Spring Boot")).thenReturn(Optional.of(skill2));
            when(skillRepository.findByNameIgnoreCase("React")).thenReturn(Optional.empty());
            when(skillRepository.save(any(Skill.class))).thenReturn(newSkill);
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.getSkills().size());
            assertTrue(result.getSkills().contains("Java"));
            assertTrue(result.getSkills().contains("Spring Boot"));
            assertTrue(result.getSkills().contains("React"));
        }

        @Test
        @DisplayName("updateWorkerProfile_givenEmptySkillNames_thenShouldFilterOutEmptyStrings")
        void updateWorkerProfile_givenEmptySkillNames_thenShouldFilterOutEmptyStrings() {
            // Arrange
            List<String> skillNames = Arrays.asList("Java", "", "   ", "Spring Boot", null);
            workerProfileDto.setSkills((Set<String>) skillNames);

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.of(skill1));
            when(skillRepository.findByNameIgnoreCase("Spring Boot")).thenReturn(Optional.of(skill2));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", workerProfileDto);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getSkills().size()); // Only valid skills should be included
            assertTrue(result.getSkills().contains("Java"));
            assertTrue(result.getSkills().contains("Spring Boot"));

            // Verify no attempts to find empty/null skills
            verify(skillRepository, never()).findByNameIgnoreCase("");
            verify(skillRepository, never()).findByNameIgnoreCase("   ");
            verify(skillRepository, never()).findByNameIgnoreCase(null);
        }
    }

    @Nested
    @DisplayName("Data Validation Tests")
    class DataValidationTests {

        @Test
        @DisplayName("updateWorkerProfile_givenPartialUpdate_thenShouldUpdateOnlyProvidedFields")
        void updateWorkerProfile_givenPartialUpdate_thenShouldUpdateOnlyProvidedFields() {
            // Arrange
            WorkerProfileDto partialDto = new WorkerProfileDto();
            partialDto.setFirstName("UpdatedName");
            partialDto.setLocation("Updated Location");
            // Other fields are null/not set

            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(workerUser));
            when(userRepository.save(any(User.class))).thenReturn(workerUser);

            // Act
            WorkerProfileDto result = profileService.updateWorkerProfile("worker@test.com", partialDto);

            // Assert
            assertNotNull(result);
            assertEquals("UpdatedName", result.getFirstName());
            assertEquals("Updated Location", result.getLocation());
            // Original values should be preserved for non-updated fields
            assertEquals("Doe", result.getLastName()); // Original value
            assertEquals("123-456-7890", result.getPhoneNumber()); // Original value
            assertEquals("Experienced developer", result.getBio()); // Original value
        }

        @Test
        @DisplayName("updateEmployerProfile_givenPartialUpdate_thenShouldUpdateOnlyProvidedFields")
        void updateEmployerProfile_givenPartialUpdate_thenShouldUpdateOnlyProvidedFields() {
            // Arrange
            EmployerProfileDto partialDto = new EmployerProfileDto();
            partialDto.setCompanyName("Updated Company");
            // Other fields are null/not set

            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.of(employerUser));
            when(userRepository.save(any(User.class))).thenReturn(employerUser);

            // Act
            EmployerProfileDto result = profileService.updateEmployerProfile("employer@test.com", partialDto);

            // Assert
            assertNotNull(result);
            assertEquals("Updated Company", result.getCompanyName());
            // Original values should be preserved for non-updated fields
            assertEquals("Leading tech company", result.getCompanyDescription()); // Original value
            assertEquals("San Francisco, CA", result.getLocation()); // Original value
        }
    }
}
