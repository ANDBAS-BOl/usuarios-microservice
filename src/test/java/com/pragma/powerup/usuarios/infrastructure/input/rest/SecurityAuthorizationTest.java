package com.pragma.powerup.usuarios.infrastructure.input.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que los endpoints del microservicio de usuarios respetan el modelo
 * de autorización definido en WebSecurityConfig y las anotaciones @PreAuthorize.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAuthorizationTest {

    private static final String SECRET   = "test_jwt_secret_key_minimum_32_chars!!";
    private static final String BASE     = "/api/v1/usuarios";
    private static final String BASE_AUTH = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String propietarioToken;
    private String empleadoToken;
    private String clienteToken;

    @BeforeEach
    void setUp() {
        adminToken      = generateToken(1L, "admin@test.com",      "ADMINISTRADOR");
        propietarioToken = generateToken(2L, "owner@test.com",     "PROPIETARIO");
        empleadoToken   = generateToken(3L, "emp@test.com",        "EMPLEADO");
        clienteToken    = generateToken(4L, "client@test.com",     "CLIENTE");
    }

    // ====================================================================
    // Endpoints públicos — no requieren token
    // ====================================================================

    @Nested
    class PublicEndpoints {

        @Test
        void loginSinToken_debePermitirAcceso() throws Exception {
            mockMvc.perform(post(BASE_AUTH + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(result -> {
                        int s = result.getResponse().getStatus();
                        org.junit.jupiter.api.Assertions.assertNotEquals(403, s,
                                "POST /login debe ser público pero devolvió 403");
                    });
        }

        @Test
        void registroClienteSinToken_debePermitirAcceso() throws Exception {
            mockMvc.perform(post(BASE + "/registro/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(result -> {
                        int s = result.getResponse().getStatus();
                        org.junit.jupiter.api.Assertions.assertNotEquals(403, s,
                                "POST /registro/cliente debe ser público pero devolvió 403");
                    });
        }

        @Test
        void swaggerUiDebeSerPublico() throws Exception {
            mockMvc.perform(get("/swagger-ui/index.html"))
                    .andExpect(result -> {
                        int s = result.getResponse().getStatus();
                        org.junit.jupiter.api.Assertions.assertTrue(
                                s == 200 || s == 302,
                                "Swagger debe ser público, status: " + s);
                    });
        }

        @Test
        void apiDocsDebeSerPublico() throws Exception {
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk());
        }
    }

    // ====================================================================
    // Sin token → 403 en endpoints protegidos
    // ====================================================================

    @Nested
    class NoTokenTests {

        @Test
        void postPropietariosSinToken_debeRechazar() throws Exception {
            mockMvc.perform(post(BASE + "/propietarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void postEmpleadosSinToken_debeRechazar() throws Exception {
            mockMvc.perform(post(BASE + "/empleados")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getValidacionPropietarioSinToken_debeRechazar() throws Exception {
            mockMvc.perform(get(BASE + "/1/validacion-propietario"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getValidacionEmpleadoSinToken_debeRechazar() throws Exception {
            mockMvc.perform(get(BASE + "/1/validacion-empleado"))
                    .andExpect(status().isForbidden());
        }
    }

    // ====================================================================
    // POST /propietarios — solo ADMINISTRADOR
    // ====================================================================

    @Nested
    class CreatePropietarioSecurity {

        private static final String URL  = BASE + "/propietarios";
        private static final String BODY = """
                {"nombre":"A","apellido":"B","documentoIdentidad":"123","celular":"+57300","correo":"a@b.com","clave":"x","fechaNacimiento":"1990-01-01"}
                """;

        @Test
        void adminDebePoderAcceder() throws Exception {
            assertNotForbidden(post(URL)
                    .header("Authorization", bearer(adminToken))
                    .contentType(MediaType.APPLICATION_JSON).content(BODY));
        }

        @ParameterizedTest
        @ValueSource(strings = {"PROPIETARIO", "EMPLEADO", "CLIENTE"})
        void otrosRolesDebenSerRechazados(String rol) throws Exception {
            mockMvc.perform(post(URL)
                            .header("Authorization", bearer(tokenForRole(rol)))
                            .contentType(MediaType.APPLICATION_JSON).content(BODY))
                    .andExpect(status().isForbidden());
        }
    }

    // ====================================================================
    // POST /empleados — solo PROPIETARIO
    // ====================================================================

    @Nested
    class CreateEmpleadoSecurity {

        private static final String URL  = BASE + "/empleados";
        private static final String BODY = """
                {"nombre":"E","apellido":"T","documentoIdentidad":"456","celular":"+57300","correo":"e@b.com","clave":"y"}
                """;

        @Test
        void propietarioDebePoderAcceder() throws Exception {
            assertNotForbidden(post(URL)
                    .header("Authorization", bearer(propietarioToken))
                    .contentType(MediaType.APPLICATION_JSON).content(BODY));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ADMINISTRADOR", "EMPLEADO", "CLIENTE"})
        void otrosRolesDebenSerRechazados(String rol) throws Exception {
            mockMvc.perform(post(URL)
                            .header("Authorization", bearer(tokenForRole(rol)))
                            .contentType(MediaType.APPLICATION_JSON).content(BODY))
                    .andExpect(status().isForbidden());
        }
    }

    // ====================================================================
    // GET /validacion-propietario — solo ADMINISTRADOR
    // ====================================================================

    @Nested
    class ValidacionPropietarioSecurity {

        private static final String URL = BASE + "/99/validacion-propietario";

        @Test
        void adminDebePoderAcceder() throws Exception {
            assertNotForbidden(get(URL).header("Authorization", bearer(adminToken)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"PROPIETARIO", "EMPLEADO", "CLIENTE"})
        void otrosRolesDebenSerRechazados(String rol) throws Exception {
            mockMvc.perform(get(URL).header("Authorization", bearer(tokenForRole(rol))))
                    .andExpect(status().isForbidden());
        }
    }

    // ====================================================================
    // GET /validacion-empleado — ADMINISTRADOR o PROPIETARIO
    // ====================================================================

    @Nested
    class ValidacionEmpleadoSecurity {

        private static final String URL = BASE + "/99/validacion-empleado";

        @Test
        void adminDebePoderAcceder() throws Exception {
            assertNotForbidden(get(URL).header("Authorization", bearer(adminToken)));
        }

        @Test
        void propietarioDebePoderAcceder() throws Exception {
            assertNotForbidden(get(URL).header("Authorization", bearer(propietarioToken)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"EMPLEADO", "CLIENTE"})
        void otrosRolesDebenSerRechazados(String rol) throws Exception {
            mockMvc.perform(get(URL).header("Authorization", bearer(tokenForRole(rol))))
                    .andExpect(status().isForbidden());
        }
    }

    // ====================================================================
    // Token inválido / expirado
    // ====================================================================

    @Nested
    class InvalidTokenTests {

        @Test
        void tokenExpirado_debeSerRechazado() throws Exception {
            String expired = generateExpiredToken(1L, "a@test.com", "ADMINISTRADOR");
            mockMvc.perform(post(BASE + "/propietarios")
                            .header("Authorization", bearer(expired))
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void tokenMalformado_debeSerRechazado() throws Exception {
            mockMvc.perform(post(BASE + "/propietarios")
                            .header("Authorization", "Bearer not.a.valid.jwt")
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void tokenConSecretoIncorrecto_debeSerRechazado() throws Exception {
            SecretKey wrongKey = Keys.hmacShaKeyFor(
                    "another_secret_key_that_is_at_least_32_chars!".getBytes(StandardCharsets.UTF_8));
            String wrongToken = Jwts.builder()
                    .setSubject("1")
                    .claim("correo", "a@test.com")
                    .claim("rol", "ADMINISTRADOR")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                    .signWith(wrongKey, SignatureAlgorithm.HS256)
                    .compact();
            mockMvc.perform(post(BASE + "/propietarios")
                            .header("Authorization", bearer(wrongToken))
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    /**
     * Verifica que la petición NO fue rechazada por Spring Security.
     * Un 403 con body {"message":...} indica que la seguridad pasó
     * y fue una excepción de dominio (AccessDeniedException propio).
     */
    private void assertNotForbidden(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder req) throws Exception {
        ResultActions result = mockMvc.perform(req);
        int status = result.andReturn().getResponse().getStatus();
        if (status == 403) {
            String body = result.andReturn().getResponse().getContentAsString();
            org.junit.jupiter.api.Assertions.assertTrue(
                    body.contains("\"message\""),
                    "El endpoint rechazó con 403 por seguridad (no lógica de negocio). Body: " + body);
        }
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String tokenForRole(String rol) {
        return switch (rol) {
            case "ADMINISTRADOR" -> adminToken;
            case "PROPIETARIO"   -> propietarioToken;
            case "EMPLEADO"      -> empleadoToken;
            case "CLIENTE"       -> clienteToken;
            default -> throw new IllegalArgumentException("Rol desconocido: " + rol);
        };
    }

    private String generateToken(Long id, String correo, String rol) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(id.toString())
                .claim("correo", correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateExpiredToken(Long id, String correo, String rol) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(id.toString())
                .claim("correo", correo)
                .claim("rol", rol)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .setExpiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
