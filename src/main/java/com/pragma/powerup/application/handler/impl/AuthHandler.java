package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.LoginResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.exception.CredencialesInvalidasException;
import com.pragma.powerup.infrastructure.security.JwtTokenProvider;
import com.pragma.powerup.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getClave()));
            UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(
                    principal.getId(), principal.getUsername(), principal.getRol().name());
            return LoginResponseDto.builder()
                    .token(token)
                    .idUsuario(principal.getId())
                    .rol(principal.getRol().name())
                    .build();
        } catch (BadCredentialsException ex) {
            throw new CredencialesInvalidasException("Correo o clave incorrectos");
        } catch (AuthenticationException ex) {
            throw new CredencialesInvalidasException("Correo o clave incorrectos");
        }
    }
}
