package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    @Size(max = 72)
    private String clave;
}
