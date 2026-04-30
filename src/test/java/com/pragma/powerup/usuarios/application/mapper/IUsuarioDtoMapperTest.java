package com.pragma.powerup.usuarios.application.mapper;

import com.pragma.powerup.usuarios.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IUsuarioDtoMapperTest {

    private IUsuarioDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUsuarioDtoMapperImpl();
    }

    // ── RegistroClienteRequestDto → UsuarioModel ──────────────────────────────

    @Test
    void toModelCliente_mapsFieldsAndIgnoresIdRolClave() {
        RegistroClienteRequestDto dto = new RegistroClienteRequestDto();
        dto.setNombre("Carlos");
        dto.setApellido("Lopez");
        dto.setDocumentoIdentidad("112233");
        dto.setCelular("+573001112233");
        dto.setCorreo("carlos@test.com");
        dto.setClave("secreto");

        UsuarioModel model = mapper.toModel(dto);

        assertEquals("Carlos", model.getNombre());
        assertEquals("Lopez", model.getApellido());
        assertEquals("112233", model.getDocumentoIdentidad());
        assertEquals("+573001112233", model.getCelular());
        assertEquals("carlos@test.com", model.getCorreo());
        assertNull(model.getId());
        assertNull(model.getClaveEncriptada());
        assertNull(model.getRol());
        assertNull(model.getFechaNacimiento());
    }

    @Test
    void toModelCliente_nullDtoReturnsNull() {
        assertNull(mapper.toModel((RegistroClienteRequestDto) null));
    }

    // ── RegistroPropietarioRequestDto → UsuarioModel ─────────────────────────

    @Test
    void toModelPropietario_mapsFieldsIncludingFechaNacimientoAndIgnoresIdRolClave() {
        RegistroPropietarioRequestDto dto = new RegistroPropietarioRequestDto();
        dto.setNombre("Maria");
        dto.setApellido("Perez");
        dto.setDocumentoIdentidad("445566");
        dto.setCelular("+573009876543");
        dto.setCorreo("maria@test.com");
        dto.setClave("claveSegura");
        dto.setFechaNacimiento(LocalDate.of(1990, 3, 15));

        UsuarioModel model = mapper.toModel(dto);

        assertEquals("Maria", model.getNombre());
        assertEquals("Perez", model.getApellido());
        assertEquals("445566", model.getDocumentoIdentidad());
        assertEquals("+573009876543", model.getCelular());
        assertEquals("maria@test.com", model.getCorreo());
        assertEquals(LocalDate.of(1990, 3, 15), model.getFechaNacimiento());
        assertNull(model.getId());
        assertNull(model.getClaveEncriptada());
        assertNull(model.getRol());
    }

    @Test
    void toModelPropietario_nullDtoReturnsNull() {
        assertNull(mapper.toModel((RegistroPropietarioRequestDto) null));
    }

    // ── RegistroEmpleadoRequestDto → UsuarioModel ─────────────────────────────

    @Test
    void toModelEmpleado_mapsFieldsAndIgnoresIdRolClaveYFecha() {
        RegistroEmpleadoRequestDto dto = new RegistroEmpleadoRequestDto();
        dto.setNombre("Luis");
        dto.setApellido("Torres");
        dto.setDocumentoIdentidad("778899");
        dto.setCelular("+573007654321");
        dto.setCorreo("luis@test.com");
        dto.setClave("claveEmp");

        UsuarioModel model = mapper.toModel(dto);

        assertEquals("Luis", model.getNombre());
        assertEquals("Torres", model.getApellido());
        assertEquals("778899", model.getDocumentoIdentidad());
        assertEquals("+573007654321", model.getCelular());
        assertEquals("luis@test.com", model.getCorreo());
        assertNull(model.getId());
        assertNull(model.getClaveEncriptada());
        assertNull(model.getRol());
        assertNull(model.getFechaNacimiento());
    }

    @Test
    void toModelEmpleado_nullDtoReturnsNull() {
        assertNull(mapper.toModel((RegistroEmpleadoRequestDto) null));
    }

    // ── UsuarioModel → UsuarioCreadoResponseDto ───────────────────────────────

    @Test
    void toCreadoResponse_mapsAllFieldsConRolComoString() {
        UsuarioModel model = UsuarioModel.builder()
                .id(10L)
                .nombre("Sofia")
                .apellido("Ramirez")
                .documentoIdentidad("321321")
                .celular("+573005551234")
                .correo("sofia@test.com")
                .claveEncriptada("$2a$10$hash")
                .rol(Rol.PROPIETARIO)
                .build();

        UsuarioCreadoResponseDto response = mapper.toCreadoResponse(model);

        assertEquals(10L, response.getId());
        assertEquals("Sofia", response.getNombre());
        assertEquals("Ramirez", response.getApellido());
        assertEquals("sofia@test.com", response.getCorreo());
        assertEquals("PROPIETARIO", response.getRol());
    }

    @Test
    void toCreadoResponse_conRolNull_mapRolComoNull() {
        UsuarioModel model = UsuarioModel.builder()
                .nombre("Test")
                .apellido("User")
                .documentoIdentidad("111222")
                .celular("+573001119999")
                .correo("test@test.com")
                .build();

        UsuarioCreadoResponseDto response = mapper.toCreadoResponse(model);

        assertNull(response.getRol());
    }

    @Test
    void toCreadoResponse_nullModelReturnsNull() {
        assertNull(mapper.toCreadoResponse(null));
    }
}
