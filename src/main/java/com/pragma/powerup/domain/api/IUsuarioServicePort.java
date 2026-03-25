package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UsuarioModel;

import java.util.Optional;

public interface IUsuarioServicePort {

    UsuarioModel registrarCliente(UsuarioModel usuario, String clavePlana);

    UsuarioModel registrarPropietario(UsuarioModel usuario, String clavePlana);

    UsuarioModel registrarEmpleado(UsuarioModel usuario, String clavePlana);

    Optional<UsuarioModel> buscarPorId(Long id);
}
