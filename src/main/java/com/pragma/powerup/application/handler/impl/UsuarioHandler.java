package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.application.handler.IUsuarioHandler;
import com.pragma.powerup.application.mapper.IUsuarioDtoMapper;
import com.pragma.powerup.domain.api.IUsuarioServicePort;
import com.pragma.powerup.domain.model.UsuarioModel;
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
}
