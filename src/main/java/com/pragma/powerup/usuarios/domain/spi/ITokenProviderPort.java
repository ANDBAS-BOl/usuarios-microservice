package com.pragma.powerup.usuarios.domain.spi;

public interface ITokenProviderPort {
    String generateToken(Long userId, String correo, String rol);
}
