package com.pragma.powerup.usuarios.application.dto.response;

public record UsuarioRoleEmpleadoValidationResponseDto(
        Long idUsuario,
        String rol,
        boolean empleadoValido
) {
}

