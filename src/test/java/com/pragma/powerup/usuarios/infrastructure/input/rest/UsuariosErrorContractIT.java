package com.pragma.powerup.usuarios.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fase 15 — Contrato de errores E2E.
 *
 * Congela los dos formatos de respuesta de error:
 *   - Bean Validation → {message: VALIDATION_FAILED, errors: {campo: msg}}
 *   - DomainException  → {message: <DomainErrorMessage.X>}        (sin errors)
 *
 * Y verifica el orden de disparo: Bean Validation → invariantes del modelo
 * → unicidad → assertEsMayorDeEdad.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuariosErrorContractIT {

    private static final String REGISTRO_CLIENTE_URL      = "/api/v1/usuarios/registro/cliente";
    private static final String REGISTRO_PROPIETARIO_URL  = "/api/v1/usuarios/propietarios";
    private static final String LOGIN_URL                 = "/api/v1/auth/login";
    private static final String VALIDATION_FAILED         =
            "Los datos enviados no superan las validaciones de campo";

    @Autowired MockMvc       mockMvc;
    @Autowired ObjectMapper  objectMapper;
    @Autowired IUsuarioRepository usuarioRepository;
    @Autowired PasswordEncoder    passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(UsuarioEntity.builder()
                .nombre("Admin")
                .apellido("Test")
                .documentoIdentidad("1")
                .celular("+10000000001")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .correo("admin@error-contract.local")
                .clave(passwordEncoder.encode("AdminTest1!"))
                .rol(Rol.ADMINISTRADOR)
                .build());
    }

    private String adminToken() throws Exception {
        String json = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@error-contract.local\",\"clave\":\"AdminTest1!\"}"))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("token").asText();
    }

    // =========================================================================
    // Contrato 1 — Bean Validation → {message: VALIDATION_FAILED, errors: {…}}
    // =========================================================================
    @Nested
    class BeanValidationContract {

        @Test
        void registroCliente_correoEnBlanco_400_conErrors() throws Exception {
            String body = """
                    {
                      "nombre": "Carlos",
                      "apellido": "Lopez",
                      "documentoIdentidad": "123456789",
                      "celular": "+573001234567",
                      "correo": "",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
                    .andExpect(jsonPath("$.errors.correo").exists());
        }

        @Test
        void registroPropietario_sinFechaNacimiento_400_conErrors() throws Exception {
            String body = """
                    {
                      "nombre": "Maria",
                      "apellido": "Gomez",
                      "documentoIdentidad": "987654321",
                      "celular": "+573009876543",
                      "correo": "maria@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_PROPIETARIO_URL)
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
                    .andExpect(jsonPath("$.errors.fechaNacimiento").exists());
        }
    }

    // =========================================================================
    // Contrato 2 — DomainException → {message: <DomainErrorMessage.X>} sin errors
    // =========================================================================
    @Nested
    class DomainExceptionContract {

        @Test
        void registroCliente_correoMalformado_400_sinErrors() throws Exception {
            String body = """
                    {
                      "nombre": "Carlos",
                      "apellido": "Lopez",
                      "documentoIdentidad": "123456789",
                      "celular": "+573001234567",
                      "correo": "noEsCorreo",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.CORREO_INVALID.getMessage()))
                    .andExpect(jsonPath("$.errors").doesNotExist());
        }

        @Test
        void registroCliente_documentoConLetras_400_sinErrors() throws Exception {
            String body = """
                    {
                      "nombre": "Carlos",
                      "apellido": "Lopez",
                      "documentoIdentidad": "ABC123",
                      "celular": "+573001234567",
                      "correo": "carlos@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.DOCUMENTO_NOT_NUMERIC.getMessage()))
                    .andExpect(jsonPath("$.errors").doesNotExist());
        }

        @Test
        void registroCliente_celularInvalido_400_sinErrors() throws Exception {
            String body = """
                    {
                      "nombre": "Carlos",
                      "apellido": "Lopez",
                      "documentoIdentidad": "123456789",
                      "celular": "123-456",
                      "correo": "carlos@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.CELULAR_INVALID.getMessage()))
                    .andExpect(jsonPath("$.errors").doesNotExist());
        }

        @Test
        void registroPropietario_menorDeEdad_400_sinErrors() throws Exception {
            String body = """
                    {
                      "nombre": "Joven",
                      "apellido": "Propietario",
                      "documentoIdentidad": "555666777",
                      "celular": "+573005556677",
                      "fechaNacimiento": "2015-01-01",
                      "correo": "joven@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_PROPIETARIO_URL)
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.PROPIETARIO_MENOR_DE_EDAD.getMessage()))
                    .andExpect(jsonPath("$.errors").doesNotExist());
        }
    }

    // =========================================================================
    // Contrato 3 — Unicidad → 409 {message: <DomainErrorMessage.X>}
    // =========================================================================
    @Nested
    class UniqueConstraintContract {

        @Test
        void registroCliente_correoDuplicado_409() throws Exception {
            String first = """
                    {
                      "nombre": "Ana",
                      "apellido": "Perez",
                      "documentoIdentidad": "111222333",
                      "celular": "+573001112233",
                      "correo": "duplicado@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(first))
                    .andExpect(status().isCreated());

            String second = """
                    {
                      "nombre": "Ana",
                      "apellido": "Perez",
                      "documentoIdentidad": "444555666",
                      "celular": "+573001112244",
                      "correo": "duplicado@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(second))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.CORREO_YA_REGISTRADO.getMessage()));
        }

        @Test
        void registroCliente_documentoDuplicado_409() throws Exception {
            String first = """
                    {
                      "nombre": "Luis",
                      "apellido": "Torres",
                      "documentoIdentidad": "777888999",
                      "celular": "+573007778899",
                      "correo": "luis@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(first))
                    .andExpect(status().isCreated());

            String second = """
                    {
                      "nombre": "Luis",
                      "apellido": "Torres",
                      "documentoIdentidad": "777888999",
                      "celular": "+573007778800",
                      "correo": "luis2@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(second))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.DOCUMENTO_YA_REGISTRADO.getMessage()));
        }
    }

    // =========================================================================
    // Orden de disparo: Bean Validation → modelo → unicidad → mayoría de edad
    // =========================================================================
    @Nested
    class DispatchOrderContract {

        /**
         * Payload con correo vacío (falla @NotBlank) Y celular inválido (falla dominio).
         * Bean Validation debe cortar el pipeline antes de llegar al modelo: se espera
         * VALIDATION_FAILED con el campo correo, nunca CELULAR_INVALID.
         */
        @Test
        void beanValidationDisparaAntesDominio() throws Exception {
            String body = """
                    {
                      "nombre": "Carlos",
                      "apellido": "Lopez",
                      "documentoIdentidad": "123456789",
                      "celular": "123-456",
                      "correo": "",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(VALIDATION_FAILED))
                    .andExpect(jsonPath("$.errors.correo").exists());
        }

        /**
         * Payload con documento con letras (falla invariante modelo) Y unicidad
         * (correo ya existente). El modelo debe lanzar antes de llegar a la BD:
         * se espera DOCUMENTO_NOT_NUMERIC, nunca CORREO_YA_REGISTRADO.
         */
        @Test
        void modeloDisparaAntesDeUnicidad() throws Exception {
            String seedConCorreo = """
                    {
                      "nombre": "Seed",
                      "apellido": "User",
                      "documentoIdentidad": "333444555",
                      "celular": "+573003334455",
                      "correo": "seed@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(seedConCorreo))
                    .andExpect(status().isCreated());

            String duplicateCorreoDocInvalid = """
                    {
                      "nombre": "Otro",
                      "apellido": "User",
                      "documentoIdentidad": "INVALIDO",
                      "celular": "+573003334400",
                      "correo": "seed@error-contract.local",
                      "clave": "Pass1234"
                    }
                    """;
            mockMvc.perform(post(REGISTRO_CLIENTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicateCorreoDocInvalid))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DomainErrorMessage.DOCUMENTO_NOT_NUMERIC.getMessage()));
        }
    }
}
