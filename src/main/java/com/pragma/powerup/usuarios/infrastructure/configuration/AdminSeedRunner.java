package com.pragma.powerup.usuarios.infrastructure.configuration;

import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class AdminSeedRunner implements ApplicationRunner {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByRol(Rol.ADMINISTRADOR)) {
            return;
        }
        UsuarioEntity admin = UsuarioEntity.builder()
                .nombre("Admin")
                .apellido("Sistema")
                .documentoIdentidad("1")
                .celular("+10000000000")
                .fechaNacimiento(java.time.LocalDate.of(1990, 1, 1))
                .correo(adminEmail)
                .clave(passwordEncoder.encode(adminPassword))
                .rol(Rol.ADMINISTRADOR)
                .build();
        usuarioRepository.save(admin);
        log.info("Usuario administrador sembrado para desarrollo: {}", adminEmail);
    }
}
