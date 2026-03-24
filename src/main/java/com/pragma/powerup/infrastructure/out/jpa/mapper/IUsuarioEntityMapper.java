package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.UsuarioModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUsuarioEntityMapper {

    @Mapping(target = "claveEncriptada", source = "clave")
    UsuarioModel toModel(UsuarioEntity entity);

    @Mapping(target = "clave", source = "claveEncriptada")
    UsuarioEntity toEntity(UsuarioModel model);
}
