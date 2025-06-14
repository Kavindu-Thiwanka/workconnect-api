package com.kt.workconnect.service;

import com.kt.workconnect.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> registerUser(RegisterRequest registerRequest);
}
