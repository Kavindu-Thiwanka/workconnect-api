package com.kt.workconnect.service;

import com.kt.workconnect.dto.RegisterRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> registerUser(RegisterRequestDTO registerRequestDTO);
}
