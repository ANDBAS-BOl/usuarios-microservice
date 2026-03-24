package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UsuarioModel;

public interface IUsuarioServicePort {

    UsuarioModel registrarCliente(UsuarioModel usuario, String clavePlana);

    UsuarioModel registrarPropietario(UsuarioModel usuario, String clavePlana);

    UsuarioModel registrarEmpleado(UsuarioModel usuario, String clavePlana);
}
