package com.pragma.powerup.usuarios.domain.model;

import com.pragma.powerup.usuarios.domain.exception.BusinessRuleException;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioModelTest {

    private static final String VALID_NOMBRE   = "Ana";
    private static final String VALID_APELLIDO = "Gomez";
    private static final String VALID_DOC      = "123456";
    private static final String VALID_CELULAR  = "+573001112233";
    private static final String VALID_CORREO   = "ana@test.com";

    private UsuarioModel.UsuarioModelBuilder validBuilder() {
        return UsuarioModel.builder()
                .nombre(VALID_NOMBRE)
                .apellido(VALID_APELLIDO)
                .documentoIdentidad(VALID_DOC)
                .celular(VALID_CELULAR)
                .correo(VALID_CORREO);
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    @Test
    void buildShouldSucceedWithAllValidFields() {
        UsuarioModel model = validBuilder()
                .id(1L)
                .fechaNacimiento(LocalDate.of(1990, 5, 20))
                .build();

        assertEquals(1L, model.getId());
        assertEquals(VALID_NOMBRE, model.getNombre());
        assertEquals(VALID_APELLIDO, model.getApellido());
        assertEquals(VALID_DOC, model.getDocumentoIdentidad());
        assertEquals(VALID_CELULAR, model.getCelular());
        assertEquals(VALID_CORREO, model.getCorreo());
        assertEquals(LocalDate.of(1990, 5, 20), model.getFechaNacimiento());
    }

    @Test
    void buildShouldSucceedWithOnlyRequiredFields() {
        assertDoesNotThrow(() -> validBuilder().build());
    }

    // ── Nombre ────────────────────────────────────────────────────────────────

    @Test
    void buildShouldFailWhenNombreIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(null)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(VALID_DOC)
                        .celular(VALID_CELULAR)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.NOMBRE_REQUERIDO.getMessage(), ex.getMessage());
    }

    @Test
    void buildShouldFailWhenNombreIsBlank() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre("   ")
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(VALID_DOC)
                        .celular(VALID_CELULAR)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.NOMBRE_REQUERIDO.getMessage(), ex.getMessage());
    }

    // ── Apellido ──────────────────────────────────────────────────────────────

    @Test
    void buildShouldFailWhenApellidoIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(null)
                        .documentoIdentidad(VALID_DOC)
                        .celular(VALID_CELULAR)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.APELLIDO_REQUERIDO.getMessage(), ex.getMessage());
    }

    @Test
    void buildShouldFailWhenApellidoIsBlank() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido("")
                        .documentoIdentidad(VALID_DOC)
                        .celular(VALID_CELULAR)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.APELLIDO_REQUERIDO.getMessage(), ex.getMessage());
    }

    // ── Documento ─────────────────────────────────────────────────────────────

    @Test
    void buildShouldFailWhenDocumentoIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(null)
                        .celular(VALID_CELULAR)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.DOCUMENTO_NOT_NUMERIC.getMessage(), ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123", "12 34", "doc-456", "123.456", "12abc"})
    void buildShouldFailWhenDocumentoContainsNonDigits(String invalidDoc) {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(invalidDoc)
                        .celular(VALID_CELULAR)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.DOCUMENTO_NOT_NUMERIC.getMessage(), ex.getMessage());
    }

    @Test
    void buildShouldSucceedWhenDocumentoIsAllDigits() {
        assertDoesNotThrow(() -> UsuarioModel.builder()
                .nombre(VALID_NOMBRE)
                .apellido(VALID_APELLIDO)
                .documentoIdentidad("99887766")
                .celular(VALID_CELULAR)
                .correo(VALID_CORREO)
                .build());
    }

    // ── Celular ───────────────────────────────────────────────────────────────

    @Test
    void buildShouldFailWhenCelularIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(VALID_DOC)
                        .celular(null)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.CELULAR_INVALID.getMessage(), ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"no-es-tel", "++573001234567", "12345678901234", "abc", "+1234567890123456"})
    void buildShouldFailWhenCelularIsInvalid(String invalidPhone) {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(VALID_DOC)
                        .celular(invalidPhone)
                        .correo(VALID_CORREO)
                        .build());
        assertEquals(DomainErrorMessage.CELULAR_INVALID.getMessage(), ex.getMessage());
    }

    @Test
    void buildShouldSucceedForCelularWithPlusPrefix() {
        assertDoesNotThrow(() -> validBuilder().celular("+573001112233").build());
    }

    @Test
    void buildShouldSucceedForCelularWithoutPlusPrefix() {
        assertDoesNotThrow(() -> validBuilder().celular("3001112233").build());
    }

    // ── Correo ────────────────────────────────────────────────────────────────

    @Test
    void buildShouldFailWhenCorreoIsNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(VALID_DOC)
                        .celular(VALID_CELULAR)
                        .correo(null)
                        .build());
        assertEquals(DomainErrorMessage.CORREO_INVALID.getMessage(), ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"noatsign", "@nodomain.com", "user@", "user@domain", "user @test.com"})
    void buildShouldFailWhenCorreoIsInvalid(String invalidEmail) {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                UsuarioModel.builder()
                        .nombre(VALID_NOMBRE)
                        .apellido(VALID_APELLIDO)
                        .documentoIdentidad(VALID_DOC)
                        .celular(VALID_CELULAR)
                        .correo(invalidEmail)
                        .build());
        assertEquals(DomainErrorMessage.CORREO_INVALID.getMessage(), ex.getMessage());
    }

    @Test
    void buildShouldSucceedForValidEmail() {
        assertDoesNotThrow(() -> validBuilder().correo("usuario.nombre+tag@sub.dominio.co").build());
    }

    // ── assertEsMayorDeEdad ───────────────────────────────────────────────────

    @Test
    void assertEsMayorDeEdad_sinFechaNacimiento_lanzaBusinessRule() {
        UsuarioModel model = validBuilder().build();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, model::assertEsMayorDeEdad);
        assertEquals(DomainErrorMessage.FECHA_NACIMIENTO_REQUERIDA.getMessage(), ex.getMessage());
    }

    @Test
    void assertEsMayorDeEdad_menorDeEdad_lanzaBusinessRule() {
        UsuarioModel model = validBuilder()
                .fechaNacimiento(LocalDate.now().minusYears(17))
                .build();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, model::assertEsMayorDeEdad);
        assertEquals(DomainErrorMessage.PROPIETARIO_MENOR_DE_EDAD.getMessage(), ex.getMessage());
    }

    @Test
    void assertEsMayorDeEdad_mayorDeEdad_noLanzaExcepcion() {
        UsuarioModel model = validBuilder()
                .fechaNacimiento(LocalDate.of(1985, 6, 15))
                .build();
        assertDoesNotThrow(model::assertEsMayorDeEdad);
    }

    @Test
    void assertEsMayorDeEdad_exactamente18Hoy_noLanzaExcepcion() {
        UsuarioModel model = validBuilder()
                .fechaNacimiento(LocalDate.now().minusYears(18))
                .build();
        assertDoesNotThrow(model::assertEsMayorDeEdad);
    }
}
