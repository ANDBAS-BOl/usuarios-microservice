package com.pragma.powerup.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreadoResponseDto {

    private Long id;
    private String correo;
    private String rol;
    private String nombre;
    private String apellido;
}
