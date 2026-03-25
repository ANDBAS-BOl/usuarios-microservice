package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UsuarioModel;
import com.pragma.powerup.domain.spi.IUsuarioPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.UsuarioEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUsuarioEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UsuarioJpaAdapter implements IUsuarioPersistencePort {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioEntityMapper usuarioEntityMapper;

    @Override
    public UsuarioModel save(UsuarioModel usuario) {
        UsuarioEntity entity = usuarioEntityMapper.toEntity(usuario);
        UsuarioEntity saved = usuarioRepository.save(entity);
        return usuarioEntityMapper.toModel(saved);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
        return usuarioRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public Optional<UsuarioModel> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).map(usuarioEntityMapper::toModel);
    }

    @Override
    public Optional<UsuarioModel> findById(Long id) {
        return usuarioRepository.findById(id).map(usuarioEntityMapper::toModel);
    }
}
