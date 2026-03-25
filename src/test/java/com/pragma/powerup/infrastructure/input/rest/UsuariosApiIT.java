package com.pragma.powerup.infrastructure.input.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.domain.model.Rol;
import com.pragma.powerup.infrastructure.out.jpa.entity.UsuarioEntity;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuariosApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(UsuarioEntity.builder()
                .nombre("Admin")
                .apellido("Test")
                .documentoIdentidad("1")
                .celular("+10000000001")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .correo("admin@test.local")
                .clave(passwordEncoder.encode("AdminTest1!"))
                .rol(Rol.ADMINISTRADOR)
                .build());
    }

    @Test
    void registroClienteYLogin_ok() throws Exception {
        String body = """
                {
                  "nombre": "Cliente",
                  "apellido": "Uno",
                  "documentoIdentidad": "100200300",
                  "celular": "+573001112233",
                  "correo": "cliente@test.local",
                  "clave": "ClientePass1"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios/registro/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.correo").value("cliente@test.local"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"cliente@test.local\",\"clave\":\"ClientePass1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void adminCreaPropietario_propietarioCreaEmpleado() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@test.local\",\"clave\":\"AdminTest1!\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String tokenAdmin = objectMapper.readTree(login.getResponse().getContentAsString()).get("token").asText();

        String propietarioJson = """
                {
                  "nombre": "Dueño",
                  "apellido": "Rest",
                  "documentoIdentidad": "200300400",
                  "celular": "+573009998877",
                  "fechaNacimiento": "1985-05-20",
                  "correo": "owner@test.local",
                  "clave": "OwnerPass123"
                }
                """;

        MvcResult createOwner = mockMvc.perform(post("/api/v1/usuarios/propietarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propietarioJson))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode ownerNode = objectMapper.readTree(createOwner.getResponse().getContentAsString());
        assertThat(ownerNode.get("rol").asText()).isEqualTo("PROPIETARIO");

        MvcResult loginOwner = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"owner@test.local\",\"clave\":\"OwnerPass123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String tokenOwner = objectMapper.readTree(loginOwner.getResponse().getContentAsString()).get("token").asText();

        String empleadoJson = """
                {
                  "nombre": "Emp",
                  "apellido": "Uno",
                  "documentoIdentidad": "300400500",
                  "celular": "+573001122334",
                  "correo": "emp@test.local",
                  "clave": "EmpPass1234"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios/empleados")
                        .header("Authorization", "Bearer " + tokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(empleadoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("EMPLEADO"));
    }

    @Test
    void login_credencialesIncorrectas_401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@test.local\",\"clave\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validacionPropietarioPorId_ok() throws Exception {
        MvcResult loginAdmin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@test.local\",\"clave\":\"AdminTest1!\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String tokenAdmin = objectMapper.readTree(loginAdmin.getResponse().getContentAsString()).get("token").asText();

        MvcResult createOwner = mockMvc.perform(post("/api/v1/usuarios/propietarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Dueño",
                                  "apellido": "Valid",
                                  "documentoIdentidad": "444555666",
                                  "celular": "+573001234567",
                                  "fechaNacimiento": "1980-01-01",
                                  "correo": "valid.owner@test.local",
                                  "clave": "OwnerPass123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        Long ownerId = objectMapper.readTree(createOwner.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/v1/usuarios/{id}/validacion-propietario", ownerId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(ownerId))
                .andExpect(jsonPath("$.rol").value("PROPIETARIO"))
                .andExpect(jsonPath("$.propietarioValido").value(true));
    }

    @Test
    void validacionEmpleadoPorId_ok() throws Exception {
        // Admin crea un propietario
        MvcResult loginAdmin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@test.local\",\"clave\":\"AdminTest1!\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String tokenAdmin = objectMapper.readTree(loginAdmin.getResponse().getContentAsString()).get("token").asText();

        MvcResult createOwner = mockMvc.perform(post("/api/v1/usuarios/propietarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Dueño",
                                  "apellido": "Valid",
                                  "documentoIdentidad": "444555667",
                                  "celular": "+573001234568",
                                  "fechaNacimiento": "1980-01-01",
                                  "correo": "valid.owner2@test.local",
                                  "clave": "OwnerPass123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        Long ownerId = objectMapper.readTree(createOwner.getResponse().getContentAsString()).get("id").asLong();

        // Login del propietario
        MvcResult loginOwner = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"valid.owner2@test.local\",\"clave\":\"OwnerPass123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String tokenOwner = objectMapper.readTree(loginOwner.getResponse().getContentAsString()).get("token").asText();

        String empleadoJson = """
                {
                  "nombre": "Emp",
                  "apellido": "Uno",
                  "documentoIdentidad": "300400501",
                  "celular": "+573001122335",
                  "correo": "emp2@test.local",
                  "clave": "EmpPass1234"
                }
                """;
        MvcResult createEmployee = mockMvc.perform(post("/api/v1/usuarios/empleados")
                        .header("Authorization", "Bearer " + tokenOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(empleadoJson))
                .andExpect(status().isCreated())
                .andReturn();
        Long employeeId = objectMapper.readTree(createEmployee.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/v1/usuarios/{id}/validacion-empleado", employeeId)
                        .header("Authorization", "Bearer " + tokenOwner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(employeeId))
                .andExpect(jsonPath("$.rol").value("EMPLEADO"))
                .andExpect(jsonPath("$.empleadoValido").value(true));
    }
}
