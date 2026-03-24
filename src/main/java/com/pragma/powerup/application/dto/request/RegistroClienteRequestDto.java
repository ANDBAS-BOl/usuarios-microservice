package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RegistroClienteRequestDto {

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotBlank
    @Size(max = 120)
    private String apellido;

    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "El documento de identidad debe ser numérico")
    @Size(max = 32)
    private String documentoIdentidad;

    @NotBlank
    @Size(max = 13, message = "El celular admite como máximo 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El celular solo puede contener dígitos y opcionalmente + al inicio")
    private String celular;

    @NotBlank
    @Email
    @Size(max = 255)
    private String correo;

    @NotBlank
    @Size(min = 8, max = 72)
    private String clave;
}
