package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.LoginResponseDto;

public interface IAuthHandler {

    LoginResponseDto login(LoginRequestDto dto);
}
