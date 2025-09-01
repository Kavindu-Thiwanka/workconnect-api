package com.workconnect.api.service.impl;

import com.workconnect.api.config.JwtUtil;
import com.workconnect.api.constants.Enum.Role;
import com.workconnect.api.constants.Enum.UserStatus;
import com.workconnect.api.dto.LoginRequestDto;
import com.workconnect.api.dto.LoginResponseDto;
import com.workconnect.api.dto.RefreshTokenRequestDto;
import com.workconnect.api.dto.RegistrationRequestDto;
import com.workconnect.api.entity.EmployerProfile;
import com.workconnect.api.entity.Profile;
import com.workconnect.api.entity.User;
import com.workconnect.api.entity.WorkerProfile;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    public User registerUser(RegistrationRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use: " + request.getEmail());
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole());
        newUser.setStatus(UserStatus.ACTIVE);

        if (request.getRole() == Role.WORKER) {
            newUser.setProfile(new WorkerProfile(newUser));
        } else if (request.getRole() == Role.EMPLOYER) {
            newUser.setProfile(new EmployerProfile(newUser));
        }

        return userRepository.save(newUser);
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public LoginResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshToken = refreshTokenRequestDto.getRefreshToken();
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = this.userRepository.findByEmail(username)
                .map(user -> new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String newAccessToken = jwtUtil.generateAccessToken(authentication);
            return new LoginResponseDto(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("Refresh token is not valid");
        }
    }
}
