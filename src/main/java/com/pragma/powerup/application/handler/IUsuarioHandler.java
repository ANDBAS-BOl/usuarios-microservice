package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.application.dto.response.UsuarioCreadoResponseDto;

public interface IUsuarioHandler {

    UsuarioCreadoResponseDto registrarCliente(RegistroClienteRequestDto dto);

    UsuarioCreadoResponseDto registrarPropietario(RegistroPropietarioRequestDto dto);

    UsuarioCreadoResponseDto registrarEmpleado(RegistroEmpleadoRequestDto dto);
}
