package com.pragma.powerup.usuarios.application.handler;

import com.pragma.powerup.usuarios.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleEmpleadoValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioCreadoResponseDto;

public interface IUsuarioHandler {

    UsuarioCreadoResponseDto registrarCliente(RegistroClienteRequestDto dto);

    UsuarioCreadoResponseDto registrarPropietario(RegistroPropietarioRequestDto dto);

    UsuarioCreadoResponseDto registrarEmpleado(RegistroEmpleadoRequestDto dto);

    UsuarioRoleValidationResponseDto validarUsuarioPropietario(Long idUsuario);

    UsuarioRoleEmpleadoValidationResponseDto validarUsuarioEmpleado(Long idUsuario);
}
