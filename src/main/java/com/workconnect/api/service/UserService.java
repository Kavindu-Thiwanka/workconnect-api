package com.workconnect.api.service;

import com.workconnect.api.dto.LoginRequestDto;
import com.workconnect.api.dto.LoginResponseDto;
import com.workconnect.api.dto.RefreshTokenRequestDto;
import com.workconnect.api.dto.RegistrationRequestDto;
import com.workconnect.api.entity.User;

public interface UserService {

    User registerUser(RegistrationRequestDto request);

    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);

    LoginResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
}
