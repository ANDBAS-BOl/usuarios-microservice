package com.pragma.powerup.usuarios.domain.model;

import com.pragma.powerup.usuarios.domain.exception.BusinessRuleException;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.Period;

@Value
@Builder(toBuilder = true)
public class UsuarioModel {

    static final String ONLY_DIGITS = "\\d+";
    static final String VALID_PHONE  = "^\\+?\\d{1,13}$";
    static final String EMAIL_REGEX  = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$";

    Long id;
    String nombre;
    String apellido;
    String documentoIdentidad;
    String celular;
    LocalDate fechaNacimiento;
    String correo;
    String claveEncriptada;
    Rol rol;

    public void assertEsMayorDeEdad() {
        if (fechaNacimiento == null) {
            throw new BusinessRuleException(DomainErrorMessage.FECHA_NACIMIENTO_REQUERIDA.getMessage());
        }
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18) {
            throw new BusinessRuleException(DomainErrorMessage.PROPIETARIO_MENOR_DE_EDAD.getMessage());
        }
    }

    public static class UsuarioModelBuilder {
        public UsuarioModel build() {
            if (nombre == null || nombre.isBlank())
                throw new BusinessRuleException(DomainErrorMessage.NOMBRE_REQUERIDO.getMessage());
            if (apellido == null || apellido.isBlank())
                throw new BusinessRuleException(DomainErrorMessage.APELLIDO_REQUERIDO.getMessage());
            if (documentoIdentidad == null || !documentoIdentidad.matches(ONLY_DIGITS))
                throw new BusinessRuleException(DomainErrorMessage.DOCUMENTO_NOT_NUMERIC.getMessage());
            if (celular == null || !celular.matches(VALID_PHONE))
                throw new BusinessRuleException(DomainErrorMessage.CELULAR_INVALID.getMessage());
            if (correo == null || !correo.matches(EMAIL_REGEX))
                throw new BusinessRuleException(DomainErrorMessage.CORREO_INVALID.getMessage());
            return new UsuarioModel(id, nombre, apellido, documentoIdentidad, celular,
                    fechaNacimiento, correo, claveEncriptada, rol);
        }
    }
}