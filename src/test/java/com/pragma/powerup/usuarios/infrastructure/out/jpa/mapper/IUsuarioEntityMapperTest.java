package com.pragma.powerup.usuarios.infrastructure.out.jpa.mapper;

import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IUsuarioEntityMapperTest {

    private IUsuarioEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUsuarioEntityMapperImpl();
    }

    // ── UsuarioEntity → UsuarioModel ──────────────────────────────────────────

    @Test
    void toModel_mapsAllFields_renombraClave() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(5L);
        entity.setNombre("Juan");
        entity.setApellido("Diaz");
        entity.setDocumentoIdentidad("654321");
        entity.setCelular("+573001234567");
        entity.setFechaNacimiento(LocalDate.of(1992, 8, 22));
        entity.setCorreo("juan@test.com");
        entity.setClave("$2a$10$encryptedHash");
        entity.setRol(Rol.PROPIETARIO);

        UsuarioModel model = mapper.toModel(entity);

        assertEquals(5L, model.getId());
        assertEquals("Juan", model.getNombre());
        assertEquals("Diaz", model.getApellido());
        assertEquals("654321", model.getDocumentoIdentidad());
        assertEquals("+573001234567", model.getCelular());
        assertEquals(LocalDate.of(1992, 8, 22), model.getFechaNacimiento());
        assertEquals("juan@test.com", model.getCorreo());
        assertEquals("$2a$10$encryptedHash", model.getClaveEncriptada());
        assertEquals(Rol.PROPIETARIO, model.getRol());
    }

    @Test
    void toModel_nullEntityReturnsNull() {
        assertNull(mapper.toModel(null));
    }

    // ── UsuarioModel → UsuarioEntity ──────────────────────────────────────────

    @Test
    void toEntity_mapsAllFields_renombradoClaveEncriptada() {
        UsuarioModel model = UsuarioModel.builder()
                .id(7L)
                .nombre("Elena")
                .apellido("Vargas")
                .documentoIdentidad("112233")
                .celular("+573009876543")
                .fechaNacimiento(LocalDate.of(1988, 4, 10))
                .correo("elena@test.com")
                .claveEncriptada("$2a$10$someHash")
                .rol(Rol.EMPLEADO)
                .build();

        UsuarioEntity entity = mapper.toEntity(model);

        assertEquals(7L, entity.getId());
        assertEquals("Elena", entity.getNombre());
        assertEquals("Vargas", entity.getApellido());
        assertEquals("112233", entity.getDocumentoIdentidad());
        assertEquals("+573009876543", entity.getCelular());
        assertEquals(LocalDate.of(1988, 4, 10), entity.getFechaNacimiento());
        assertEquals("elena@test.com", entity.getCorreo());
        assertEquals("$2a$10$someHash", entity.getClave());
        assertEquals(Rol.EMPLEADO, entity.getRol());
    }

    @Test
    void toEntity_nullModelReturnsNull() {
        assertNull(mapper.toEntity(null));
    }

    // ── Bidireccionalidad ─────────────────────────────────────────────────────

    @Test
    void roundTrip_entityToModelToEntity_preservesAllFields() {
        UsuarioEntity original = new UsuarioEntity();
        original.setId(99L);
        original.setNombre("Redondo");
        original.setApellido("Trip");
        original.setDocumentoIdentidad("555666");
        original.setCelular("+573001115566");
        original.setFechaNacimiento(LocalDate.of(1995, 1, 1));
        original.setCorreo("round@test.com");
        original.setClave("$2a$10$roundHash");
        original.setRol(Rol.CLIENTE);

        UsuarioModel model = mapper.toModel(original);
        UsuarioEntity rebuilt = mapper.toEntity(model);

        assertEquals(original.getId(), rebuilt.getId());
        assertEquals(original.getNombre(), rebuilt.getNombre());
        assertEquals(original.getApellido(), rebuilt.getApellido());
        assertEquals(original.getDocumentoIdentidad(), rebuilt.getDocumentoIdentidad());
        assertEquals(original.getCelular(), rebuilt.getCelular());
        assertEquals(original.getFechaNacimiento(), rebuilt.getFechaNacimiento());
        assertEquals(original.getCorreo(), rebuilt.getCorreo());
        assertEquals(original.getClave(), rebuilt.getClave());
        assertEquals(original.getRol(), rebuilt.getRol());
    }
}
