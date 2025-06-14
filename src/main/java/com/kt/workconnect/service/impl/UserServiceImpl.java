package com.kt.workconnect.service.impl;

import com.kt.workconnect.dto.UserProfileResponseDTO;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.repository.UserRepository;
import com.kt.workconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserProfileResponseDTO profile = UserProfileResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(profile);
    }
}
