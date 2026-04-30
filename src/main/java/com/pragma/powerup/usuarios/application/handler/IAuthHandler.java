package com.pragma.powerup.usuarios.application.handler;

import com.pragma.powerup.usuarios.application.dto.request.LoginRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.LoginResponseDto;

public interface IAuthHandler {

    LoginResponseDto login(LoginRequestDto dto);
}
