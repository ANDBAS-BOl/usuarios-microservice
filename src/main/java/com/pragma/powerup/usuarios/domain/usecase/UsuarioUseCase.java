package com.pragma.powerup.usuarios.domain.usecase;

import com.pragma.powerup.usuarios.domain.api.IUsuarioServicePort;
import com.pragma.powerup.usuarios.domain.exception.ConflictoException;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import com.pragma.powerup.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.usuarios.domain.spi.IUsuarioPersistencePort;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;

import java.util.Optional;

public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort, IPasswordEncoderPort passwordEncoderPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public UsuarioModel registrarCliente(UsuarioModel usuario, String clavePlana) {
        validarCorreoYDocumentoUnicos(usuario.getCorreo(), usuario.getDocumentoIdentidad());
        UsuarioModel toSave = usuario.toBuilder()
                .rol(Rol.CLIENTE)
                .fechaNacimiento(null)
                .claveEncriptada(passwordEncoderPort.encode(clavePlana))
                .build();
        return usuarioPersistencePort.save(toSave);
    }

    @Override
    public UsuarioModel registrarPropietario(UsuarioModel usuario, String clavePlana) {
        validarCorreoYDocumentoUnicos(usuario.getCorreo(), usuario.getDocumentoIdentidad());
        usuario.assertEsMayorDeEdad();
        UsuarioModel toSave = usuario.toBuilder()
                .rol(Rol.PROPIETARIO)
                .claveEncriptada(passwordEncoderPort.encode(clavePlana))
                .build();
        return usuarioPersistencePort.save(toSave);
    }

    @Override
    public UsuarioModel registrarEmpleado(UsuarioModel usuario, String clavePlana) {
        validarCorreoYDocumentoUnicos(usuario.getCorreo(), usuario.getDocumentoIdentidad());
        UsuarioModel toSave = usuario.toBuilder()
                .rol(Rol.EMPLEADO)
                .fechaNacimiento(null)
                .claveEncriptada(passwordEncoderPort.encode(clavePlana))
                .build();
        return usuarioPersistencePort.save(toSave);
    }

    @Override
    public Optional<UsuarioModel> buscarPorId(Long id) {
        return usuarioPersistencePort.findById(id);
    }

    private void validarCorreoYDocumentoUnicos(String correo, String documento) {
        if (usuarioPersistencePort.existsByCorreo(correo)) {
            throw new ConflictoException(DomainErrorMessage.CORREO_YA_REGISTRADO.getMessage());
        }
        if (usuarioPersistencePort.existsByDocumentoIdentidad(documento)) {
            throw new ConflictoException(DomainErrorMessage.DOCUMENTO_YA_REGISTRADO.getMessage());
        }
    }
}