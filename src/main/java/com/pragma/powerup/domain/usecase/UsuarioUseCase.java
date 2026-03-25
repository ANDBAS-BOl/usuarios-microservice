package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUsuarioServicePort;
import com.pragma.powerup.domain.exception.ConflictoException;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Rol;
import com.pragma.powerup.domain.model.UsuarioModel;
import com.pragma.powerup.domain.spi.IUsuarioPersistencePort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class UsuarioUseCase implements IUsuarioServicePort {

    private static final int MAYORIA_EDAD_ANIOS = 18;

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final PasswordEncoder passwordEncoder;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort, PasswordEncoder passwordEncoder) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioModel registrarCliente(UsuarioModel usuario, String clavePlana) {
        assertCorreoYDocumentoUnicos(usuario.getCorreo(), usuario.getDocumentoIdentidad());
        usuario.setRol(Rol.CLIENTE);
        usuario.setFechaNacimiento(null);
        usuario.setClaveEncriptada(passwordEncoder.encode(clavePlana));
        return usuarioPersistencePort.save(usuario);
    }

    @Override
    public UsuarioModel registrarPropietario(UsuarioModel usuario, String clavePlana) {
        assertCorreoYDocumentoUnicos(usuario.getCorreo(), usuario.getDocumentoIdentidad());
        if (usuario.getFechaNacimiento() == null) {
            throw new DomainException("La fecha de nacimiento es obligatoria para el propietario");
        }
        if (!esMayorDeEdad(usuario.getFechaNacimiento())) {
            throw new DomainException("El propietario debe ser mayor de edad");
        }
        usuario.setRol(Rol.PROPIETARIO);
        usuario.setClaveEncriptada(passwordEncoder.encode(clavePlana));
        return usuarioPersistencePort.save(usuario);
    }

    @Override
    public UsuarioModel registrarEmpleado(UsuarioModel usuario, String clavePlana) {
        assertCorreoYDocumentoUnicos(usuario.getCorreo(), usuario.getDocumentoIdentidad());
        usuario.setRol(Rol.EMPLEADO);
        usuario.setFechaNacimiento(null);
        usuario.setClaveEncriptada(passwordEncoder.encode(clavePlana));
        return usuarioPersistencePort.save(usuario);
    }

    @Override
    public Optional<UsuarioModel> buscarPorId(Long id) {
        return usuarioPersistencePort.findById(id);
    }

    private void assertCorreoYDocumentoUnicos(String correo, String documento) {
        if (usuarioPersistencePort.existsByCorreo(correo)) {
            throw new ConflictoException("Ya existe un usuario con el correo indicado");
        }
        if (usuarioPersistencePort.existsByDocumentoIdentidad(documento)) {
            throw new ConflictoException("Ya existe un usuario con el documento de identidad indicado");
        }
    }

    private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= MAYORIA_EDAD_ANIOS;
    }
}
