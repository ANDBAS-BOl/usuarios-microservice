package com.pragma.powerup.usuarios.application.handler.impl;

import com.pragma.powerup.usuarios.application.dto.request.LoginRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.LoginResponseDto;
import com.pragma.powerup.usuarios.application.handler.IAuthHandler;
import com.pragma.powerup.usuarios.application.handler.IAuthPrincipal;
import com.pragma.powerup.usuarios.domain.exception.CredencialesInvalidasException;
import com.pragma.powerup.usuarios.domain.spi.ITokenProviderPort;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;
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
    private final ITokenProviderPort tokenProviderPort;

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getClave()));
            IAuthPrincipal principal = (IAuthPrincipal) authentication.getPrincipal();
            String token = tokenProviderPort.generateToken(
                    principal.getId(), principal.getUsername(), principal.getRolName());
            return LoginResponseDto.builder()
                    .token(token)
                    .idUsuario(principal.getId())
                    .rol(principal.getRolName())
                    .build();
        } catch (BadCredentialsException ex) {
            throw new CredencialesInvalidasException(DomainErrorMessage.CREDENCIALES_INVALIDAS.getMessage());
        } catch (AuthenticationException ex) {
            throw new CredencialesInvalidasException(DomainErrorMessage.CREDENCIALES_INVALIDAS.getMessage());
        }
    }
}
