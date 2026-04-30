package com.pragma.powerup.usuarios.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class RegistroPropietarioRequestDto {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private String documentoIdentidad;

    @NotBlank
    private String celular;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotBlank
    private String correo;

    @NotBlank
    private String clave;
}