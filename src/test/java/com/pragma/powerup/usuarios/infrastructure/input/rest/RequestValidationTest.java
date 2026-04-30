package com.pragma.powerup.usuarios.infrastructure.input.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que cuerpos inválidos (campos faltantes o vacíos) devuelven 400
 * con payload {message, errors} — no 500 ni 200.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RequestValidationTest {

    private static final String SECRET = "test_jwt_secret_key_minimum_32_chars!!";
    private static final String BASE_USUARIOS = "/api/v1/usuarios";
    private static final String BASE_AUTH     = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String propietarioToken;

    @BeforeEach
    void setUp() {
        adminToken     = generateToken(1L, "ADMINISTRADOR");
        propietarioToken = generateToken(2L, "PROPIETARIO");
    }

    // ── HU 8 — registro cliente (público) ────────────────────────────────────

    @Test
    void registroCliente_cuerpoVacio_shouldReturn400ConErrors() throws Exception {
        mockMvc.perform(post(BASE_USUARIOS + "/registro/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void registroCliente_camposEnBlanco_shouldReturn400ConCamposAfectados() throws Exception {
        String body = """
                {"nombre":"","apellido":"","documentoIdentidad":"","celular":"","correo":"","clave":""}
                """;
        mockMvc.perform(post(BASE_USUARIOS + "/registro/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.apellido").exists())
                .andExpect(jsonPath("$.errors.documentoIdentidad").exists())
                .andExpect(jsonPath("$.errors.celular").exists())
                .andExpect(jsonPath("$.errors.correo").exists())
                .andExpect(jsonPath("$.errors.clave").exists());
    }

    @Test
    void registroCliente_jsonMalformado_shouldReturn400ConMessage() throws Exception {
        mockMvc.perform(post(BASE_USUARIOS + "/registro/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── HU 1 — registro propietario (ADMINISTRADOR) ───────────────────────────

    @Test
    void registroPropietario_cuerpoVacio_shouldReturn400ConErrors() throws Exception {
        mockMvc.perform(post(BASE_USUARIOS + "/propietarios")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void registroPropietario_camposEnBlanco_shouldReturn400ConCamposAfectados() throws Exception {
        String body = """
                {"nombre":"","apellido":"","documentoIdentidad":"","celular":"","correo":"","clave":"","fechaNacimiento":null}
                """;
        mockMvc.perform(post(BASE_USUARIOS + "/propietarios")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.apellido").exists());
    }

    @Test
    void registroPropietario_sinFechaNacimiento_shouldReturn400() throws Exception {
        String body = """
                {"nombre":"A","apellido":"B","documentoIdentidad":"123","celular":"+573001112233","correo":"a@b.com","clave":"pass"}
                """;
        mockMvc.perform(post(BASE_USUARIOS + "/propietarios")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.fechaNacimiento").exists());
    }

    // ── HU 6 — registro empleado (PROPIETARIO) ────────────────────────────────

    @Test
    void registroEmpleado_cuerpoVacio_shouldReturn400ConErrors() throws Exception {
        mockMvc.perform(post(BASE_USUARIOS + "/empleados")
                        .header("Authorization", bearer(propietarioToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void registroEmpleado_camposEnBlanco_shouldReturn400ConCamposAfectados() throws Exception {
        String body = """
                {"nombre":"","apellido":"","documentoIdentidad":"","celular":"","correo":"","clave":""}
                """;
        mockMvc.perform(post(BASE_USUARIOS + "/empleados")
                        .header("Authorization", bearer(propietarioToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.apellido").exists());
    }

    // ── HU 5 — login (público) ────────────────────────────────────────────────

    @Test
    void login_cuerpoVacio_shouldReturn400ConErrors() throws Exception {
        mockMvc.perform(post(BASE_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void login_camposEnBlanco_shouldReturn400ConCamposAfectados() throws Exception {
        String body = """
                {"correo":"","clave":""}
                """;
        mockMvc.perform(post(BASE_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.correo").exists())
                .andExpect(jsonPath("$.errors.clave").exists());
    }

    @Test
    void login_jsonMalformado_shouldReturn400ConMessage() throws Exception {
        mockMvc.perform(post(BASE_AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String generateToken(Long id, String rol) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(id.toString())
                .claim("correo", "user@test.com")
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
