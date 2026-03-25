package com.pragma.powerup.application.dto.response;

public record UsuarioRoleValidationResponseDto(
        Long idUsuario,
        String rol,
        boolean propietarioValido
) {
}
