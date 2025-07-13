package com.workconnect.api.controller;

import com.workconnect.api.dto.LoginRequestDto;
import com.workconnect.api.dto.LoginResponseDto;
import com.workconnect.api.dto.RefreshTokenRequestDto;
import com.workconnect.api.dto.RegistrationRequestDto;
import com.workconnect.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserService userService;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequestDto requestDto) {
        userService.registerUser(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = userService.loginUser(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        LoginResponseDto response = userService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
