package com.workconnect.api.service;

import com.workconnect.api.config.JwtUtil;
import com.workconnect.api.constants.Enum.*;
import com.workconnect.api.dto.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private RegistrationRequestDto workerRegistrationDto;
    private RegistrationRequestDto employerRegistrationDto;
    private LoginRequestDto loginRequestDto;
    private RefreshTokenRequestDto refreshTokenRequestDto;
    private User existingWorkerUser;
    private User existingEmployerUser;

    @BeforeEach
    void setUp() {
        // Setup registration DTOs
        workerRegistrationDto = new RegistrationRequestDto();
        workerRegistrationDto.setEmail("worker@test.com");
        workerRegistrationDto.setPassword("password123");
        workerRegistrationDto.setRole(Role.WORKER);

        employerRegistrationDto = new RegistrationRequestDto();
        employerRegistrationDto.setEmail("employer@test.com");
        employerRegistrationDto.setPassword("password123");
        employerRegistrationDto.setRole(Role.EMPLOYER);

        // Setup login DTO
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("worker@test.com");
        loginRequestDto.setPassword("password123");

        // Setup refresh token DTO
        refreshTokenRequestDto = new RefreshTokenRequestDto();
        refreshTokenRequestDto.setRefreshToken("valid-refresh-token");

        // Setup existing users
        existingWorkerUser = new User();
        existingWorkerUser.setUserId(1L);
        existingWorkerUser.setEmail("worker@test.com");
        existingWorkerUser.setPassword("encoded-password");
        existingWorkerUser.setRole(Role.WORKER);
        existingWorkerUser.setStatus(UserStatus.ACTIVE);
        existingWorkerUser.setProfile(new WorkerProfile(existingWorkerUser));

        existingEmployerUser = new User();
        existingEmployerUser.setUserId(2L);
        existingEmployerUser.setEmail("employer@test.com");
        existingEmployerUser.setPassword("encoded-password");
        existingEmployerUser.setRole(Role.EMPLOYER);
        existingEmployerUser.setStatus(UserStatus.ACTIVE);
        existingEmployerUser.setProfile(new EmployerProfile(existingEmployerUser));
    }

    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {

        @Test
        @DisplayName("registerUser_givenValidWorkerRegistration_thenShouldCreateWorkerWithProfile")
        void registerUser_givenValidWorkerRegistration_thenShouldCreateWorkerWithProfile() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setUserId(1L);
                return user;
            });

            // Act
            User result = userService.registerUser(workerRegistrationDto);

            // Assert
            assertNotNull(result);
            assertEquals("worker@test.com", result.getEmail());
            assertEquals("encoded-password", result.getPassword());
            assertEquals(Role.WORKER, result.getRole());
            assertEquals(UserStatus.ACTIVE, result.getStatus());
            assertNotNull(result.getProfile());
            assertTrue(result.getProfile() instanceof WorkerProfile);
            
            verify(userRepository).findByEmail("worker@test.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("registerUser_givenValidEmployerRegistration_thenShouldCreateEmployerWithProfile")
        void registerUser_givenValidEmployerRegistration_thenShouldCreateEmployerWithProfile() {
            // Arrange
            when(userRepository.findByEmail("employer@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setUserId(2L);
                return user;
            });

            // Act
            User result = userService.registerUser(employerRegistrationDto);

            // Assert
            assertNotNull(result);
            assertEquals("employer@test.com", result.getEmail());
            assertEquals(Role.EMPLOYER, result.getRole());
            assertNotNull(result.getProfile());
            assertTrue(result.getProfile() instanceof EmployerProfile);
            
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("registerUser_givenExistingEmail_thenShouldThrowIllegalStateException")
        void registerUser_givenExistingEmail_thenShouldThrowIllegalStateException() {
            // Arrange
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(existingWorkerUser));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
                userService.registerUser(workerRegistrationDto));
            
            assertTrue(exception.getMessage().contains("Email already in use"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("registerUser_givenNullRole_thenShouldHandleGracefully")
        void registerUser_givenNullRole_thenShouldHandleGracefully() {
            // Arrange
            workerRegistrationDto.setRole(null);
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User result = userService.registerUser(workerRegistrationDto);

            // Assert
            assertNotNull(result);
            assertNull(result.getRole());
            assertNull(result.getProfile()); // No profile created for null role
        }
    }

    @Nested
    @DisplayName("User Authentication Tests")
    class UserAuthenticationTests {

        @Test
        @DisplayName("loginUser_givenValidCredentials_thenShouldReturnTokens")
        void loginUser_givenValidCredentials_thenShouldReturnTokens() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
            when(jwtUtil.generateAccessToken(authentication)).thenReturn("access-token");
            when(jwtUtil.generateRefreshToken(authentication)).thenReturn("refresh-token");

            // Act
            LoginResponseDto result = userService.loginUser(loginRequestDto);

            // Assert
            assertNotNull(result);
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());
            
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtil).generateAccessToken(authentication);
            verify(jwtUtil).generateRefreshToken(authentication);
        }

        @Test
        @DisplayName("loginUser_givenInvalidCredentials_thenShouldThrowBadCredentialsException")
        void loginUser_givenInvalidCredentials_thenShouldThrowBadCredentialsException() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> 
                userService.loginUser(loginRequestDto));
            
            verify(jwtUtil, never()).generateAccessToken(any());
            verify(jwtUtil, never()).generateRefreshToken(any());
        }

        @Test
        @DisplayName("loginUser_givenNullCredentials_thenShouldHandleGracefully")
        void loginUser_givenNullCredentials_thenShouldHandleGracefully() {
            // Arrange
            LoginRequestDto nullLoginDto = new LoginRequestDto();
            nullLoginDto.setEmail(null);
            nullLoginDto.setPassword(null);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> 
                userService.loginUser(nullLoginDto));
        }
    }

    @Nested
    @DisplayName("Token Refresh Tests")
    class TokenRefreshTests {

        @Test
        @DisplayName("refreshToken_givenInvalidRefreshToken_thenShouldThrowException")
        void refreshToken_givenInvalidRefreshToken_thenShouldThrowException() {
            // Arrange
            when(jwtUtil.extractUsername("invalid-refresh-token")).thenReturn("worker@test.com");
            when(userRepository.findByEmail("worker@test.com")).thenReturn(Optional.of(existingWorkerUser));

            refreshTokenRequestDto.setRefreshToken("invalid-refresh-token");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> 
                userService.refreshToken(refreshTokenRequestDto));
            
            verify(jwtUtil, never()).generateAccessToken(any());
            verify(jwtUtil, never()).generateRefreshToken(any());
        }

        @Test
        @DisplayName("refreshToken_givenNonExistentUser_thenShouldThrowUsernameNotFoundException")
        void refreshToken_givenNonExistentUser_thenShouldThrowUsernameNotFoundException() {
            // Arrange
            when(jwtUtil.extractUsername("valid-refresh-token")).thenReturn("nonexistent@test.com");
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class, () -> 
                userService.refreshToken(refreshTokenRequestDto));
        }
    }
}
