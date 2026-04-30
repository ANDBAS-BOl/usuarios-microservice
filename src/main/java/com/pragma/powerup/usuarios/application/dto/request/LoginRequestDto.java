package com.pragma.powerup.usuarios.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    private String correo;

    @NotBlank
    private String clave;
}