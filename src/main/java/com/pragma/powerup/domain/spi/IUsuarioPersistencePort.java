package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UsuarioModel;

import java.util.Optional;

public interface IUsuarioPersistencePort {

    UsuarioModel save(UsuarioModel usuario);

    boolean existsByCorreo(String correo);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    Optional<UsuarioModel> findByCorreo(String correo);

    Optional<UsuarioModel> findById(Long id);
}
