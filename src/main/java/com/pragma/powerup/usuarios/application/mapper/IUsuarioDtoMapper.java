package com.pragma.powerup.usuarios.application.mapper;

import com.pragma.powerup.usuarios.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUsuarioDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claveEncriptada", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    UsuarioModel toModel(RegistroClienteRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claveEncriptada", ignore = true)
    @Mapping(target = "rol", ignore = true)
    UsuarioModel toModel(RegistroPropietarioRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claveEncriptada", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    UsuarioModel toModel(RegistroEmpleadoRequestDto dto);

    @Mapping(target = "rol", expression = "java(model.getRol() == null ? null : model.getRol().name())")
    UsuarioCreadoResponseDto toCreadoResponse(UsuarioModel model);
}
