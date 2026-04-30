package com.pragma.powerup.usuarios.infrastructure.out.jpa.repository;

import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    Optional<UsuarioEntity> findByCorreo(String correo);

    boolean existsByRol(Rol rol);
}
