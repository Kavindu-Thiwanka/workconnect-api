package com.kt.workconnect.service;

import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> findUserByEmail(String email);
}
