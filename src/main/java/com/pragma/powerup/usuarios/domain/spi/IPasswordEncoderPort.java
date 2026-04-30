package com.pragma.powerup.usuarios.domain.spi;

public interface IPasswordEncoderPort {

    String encode(String rawPassword);
}
