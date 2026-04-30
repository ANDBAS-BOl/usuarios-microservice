package com.pragma.powerup.usuarios.infrastructure.out.security.adapter;

import com.pragma.powerup.usuarios.domain.spi.ITokenProviderPort;
import com.pragma.powerup.usuarios.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProviderAdapter implements ITokenProviderPort {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String generateToken(Long userId, String correo, String rol) {
        return jwtTokenProvider.generateToken(userId, correo, rol);
    }
}
