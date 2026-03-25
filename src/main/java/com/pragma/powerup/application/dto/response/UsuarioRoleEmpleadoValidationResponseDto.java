package com.pragma.powerup.application.dto.response;

public record UsuarioRoleEmpleadoValidationResponseDto(
        Long idUsuario,
        String rol,
        boolean empleadoValido
) {
}

