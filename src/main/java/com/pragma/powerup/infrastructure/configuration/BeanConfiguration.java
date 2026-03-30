package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IUsuarioServicePort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUsuarioPersistencePort;
import com.pragma.powerup.domain.usecase.UsuarioUseCase;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UsuarioJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUsuarioEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUsuarioRepository;
import com.pragma.powerup.infrastructure.out.security.adapter.PasswordEncoderAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioEntityMapper usuarioEntityMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public IUsuarioPersistencePort usuarioPersistencePort() {
        return new UsuarioJpaAdapter(usuarioRepository, usuarioEntityMapper);
    }

    @Bean
    public IPasswordEncoderPort passwordEncoderPort() {
        return new PasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public IUsuarioServicePort usuarioServicePort() {
        return new UsuarioUseCase(usuarioPersistencePort(), passwordEncoderPort());
    }
}
