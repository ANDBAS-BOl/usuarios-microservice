package com.pragma.powerup.usuarios.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RegistroClienteRequestDto {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private String documentoIdentidad;

    @NotBlank
    private String celular;

    @NotBlank
    private String correo;

    @NotBlank
    private String clave;
}