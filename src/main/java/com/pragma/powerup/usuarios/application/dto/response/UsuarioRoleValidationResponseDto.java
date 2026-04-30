package com.pragma.powerup.usuarios.application.dto.response;

public record UsuarioRoleValidationResponseDto(
        Long idUsuario,
        String rol,
        boolean propietarioValido
) {
}
