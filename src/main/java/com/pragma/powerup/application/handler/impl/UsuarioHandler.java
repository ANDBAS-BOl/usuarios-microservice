package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.application.dto.response.UsuarioRoleEmpleadoValidationResponseDto;
import com.pragma.powerup.application.dto.response.UsuarioRoleValidationResponseDto;
import com.pragma.powerup.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.application.handler.IUsuarioHandler;
import com.pragma.powerup.application.mapper.IUsuarioDtoMapper;
import com.pragma.powerup.domain.api.IUsuarioServicePort;
import com.pragma.powerup.domain.model.UsuarioModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No existe usuario con id " + idUsuario));
        boolean esPropietario = usuario.getRol() != null && "PROPIETARIO".equals(usuario.getRol().name());
        return new UsuarioRoleValidationResponseDto(
                usuario.getId(),
                usuario.getRol().name(),
                esPropietario
        );
    }

    @Override
    public UsuarioRoleEmpleadoValidationResponseDto validarUsuarioEmpleado(Long idUsuario) {
        UsuarioModel usuario = usuarioServicePort.buscarPorId(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No existe usuario con id " + idUsuario));
        boolean esEmpleado = usuario.getRol() != null && "EMPLEADO".equals(usuario.getRol().name());
        return new UsuarioRoleEmpleadoValidationResponseDto(
                usuario.getId(),
                usuario.getRol().name(),
                esEmpleado
        );
    }
}
