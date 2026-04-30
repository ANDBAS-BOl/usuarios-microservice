package com.pragma.powerup.usuarios.application.handler.impl;

import com.pragma.powerup.usuarios.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleEmpleadoValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.usuarios.application.handler.IUsuarioHandler;
import com.pragma.powerup.usuarios.application.mapper.IUsuarioDtoMapper;
import com.pragma.powerup.usuarios.domain.api.IUsuarioServicePort;
import com.pragma.powerup.usuarios.domain.exception.ResourceNotFoundException;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioHandler implements IUsuarioHandler {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioDtoMapper usuarioDtoMapper;

    @Override
    public UsuarioCreadoResponseDto registrarCliente(RegistroClienteRequestDto dto) {
        UsuarioModel model = usuarioDtoMapper.toModel(dto);
        UsuarioModel saved = usuarioServicePort.registrarCliente(model, dto.getClave());
        return usuarioDtoMapper.toCreadoResponse(saved);
    }

    @Override
    public UsuarioCreadoResponseDto registrarPropietario(RegistroPropietarioRequestDto dto) {
        UsuarioModel model = usuarioDtoMapper.toModel(dto);
        UsuarioModel saved = usuarioServicePort.registrarPropietario(model, dto.getClave());
        return usuarioDtoMapper.toCreadoResponse(saved);
    }

    @Override
    public UsuarioCreadoResponseDto registrarEmpleado(RegistroEmpleadoRequestDto dto) {
        UsuarioModel model = usuarioDtoMapper.toModel(dto);
        UsuarioModel saved = usuarioServicePort.registrarEmpleado(model, dto.getClave());
        return usuarioDtoMapper.toCreadoResponse(saved);
    }

    @Override
    public UsuarioRoleValidationResponseDto validarUsuarioPropietario(Long idUsuario) {
        UsuarioModel usuario = usuarioServicePort.buscarPorId(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(DomainErrorMessage.USUARIO_NOT_FOUND.getMessage()));
        return new UsuarioRoleValidationResponseDto(
                usuario.getId(),
                usuario.getRol().name(),
                usuario.getRol() == Rol.PROPIETARIO
        );
    }

    @Override
    public UsuarioRoleEmpleadoValidationResponseDto validarUsuarioEmpleado(Long idUsuario) {
        UsuarioModel usuario = usuarioServicePort.buscarPorId(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(DomainErrorMessage.USUARIO_NOT_FOUND.getMessage()));
        return new UsuarioRoleEmpleadoValidationResponseDto(
                usuario.getId(),
                usuario.getRol().name(),
                usuario.getRol() == Rol.EMPLEADO
        );
    }
}
