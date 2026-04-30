package com.pragma.powerup.usuarios.domain.utils;

public enum DomainErrorMessage {

    // Formato (HU 1, 6, 8)
    NOMBRE_REQUERIDO("El nombre es obligatorio"),
    APELLIDO_REQUERIDO("El apellido es obligatorio"),
    DOCUMENTO_NOT_NUMERIC("El documento de identidad debe ser numerico"),
    CELULAR_INVALID("El celular admite como maximo 13 caracteres y solo digitos con + opcional"),
    CORREO_INVALID("El correo no tiene una estructura valida"),
    CLAVE_REQUERIDA("La clave es obligatoria"),

    // Propietario (HU 1)
    FECHA_NACIMIENTO_REQUERIDA("La fecha de nacimiento es obligatoria para el propietario"),
    PROPIETARIO_MENOR_DE_EDAD("El propietario debe ser mayor de edad"),

    // Unicidad
    CORREO_YA_REGISTRADO("Ya existe un usuario con el correo indicado"),
    DOCUMENTO_YA_REGISTRADO("Ya existe un usuario con el documento de identidad indicado"),

    // Autenticacion (HU 5)
    CREDENCIALES_INVALIDAS("Correo o clave incorrectos"),

    // Lookup (validaciones cruzadas usadas por plazoleta)
    USUARIO_NOT_FOUND("Usuario no existe");

    private final String message;

    DomainErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
