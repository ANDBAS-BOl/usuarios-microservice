package com.pragma.powerup.usuarios.infrastructure.out.jpa.adapter;

import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.mapper.IUsuarioEntityMapper;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioJpaAdapterTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IUsuarioEntityMapper usuarioEntityMapper;

    private UsuarioJpaAdapter adapter;

    private static final Long DB_ASSIGNED_ID = 99L;

    private UsuarioModel buildInputModel() {
        return UsuarioModel.builder()
                .nombre("Ana")
                .apellido("Lopez")
                .documentoIdentidad("123456")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.of(1990, 6, 15))
                .correo("ana@test.com")
                .claveEncriptada("$2a$10$hash")
                .rol(Rol.CLIENTE)
                .build();
    }

    private UsuarioEntity stubEntity(Long id) {
        UsuarioEntity e = new UsuarioEntity();
        e.setId(id);
        e.setNombre("Ana");
        e.setApellido("Lopez");
        e.setDocumentoIdentidad("123456");
        e.setCelular("+573001234567");
        e.setFechaNacimiento(LocalDate.of(1990, 6, 15));
        e.setCorreo("ana@test.com");
        e.setClave("$2a$10$hash");
        e.setRol(Rol.CLIENTE);
        return e;
    }

    @BeforeEach
    void setUp() {
        adapter = new UsuarioJpaAdapter(usuarioRepository, usuarioEntityMapper);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_returnsModelWithIdAssignedByDb() {
        UsuarioModel input = buildInputModel();
        UsuarioEntity entitySinId = stubEntity(null);
        UsuarioEntity entityConId = stubEntity(DB_ASSIGNED_ID);
        UsuarioModel expectedModel = input.toBuilder().id(DB_ASSIGNED_ID).build();

        when(usuarioEntityMapper.toEntity(input)).thenReturn(entitySinId);
        when(usuarioRepository.save(entitySinId)).thenReturn(entityConId);
        when(usuarioEntityMapper.toModel(entityConId)).thenReturn(expectedModel);

        UsuarioModel result = adapter.save(input);

        assertEquals(DB_ASSIGNED_ID, result.getId());
    }

    @Test
    void save_delegatesMapperCallsInOrder_preservingImmutability() {
        UsuarioModel input = buildInputModel();
        UsuarioEntity entitySinId = stubEntity(null);
        UsuarioEntity entityConId = stubEntity(DB_ASSIGNED_ID);
        UsuarioModel modelConId = input.toBuilder().id(DB_ASSIGNED_ID).build();

        when(usuarioEntityMapper.toEntity(input)).thenReturn(entitySinId);
        when(usuarioRepository.save(entitySinId)).thenReturn(entityConId);
        when(usuarioEntityMapper.toModel(entityConId)).thenReturn(modelConId);

        adapter.save(input);

        ArgumentCaptor<UsuarioModel> modelCaptor = ArgumentCaptor.forClass(UsuarioModel.class);
        ArgumentCaptor<UsuarioEntity> entityCaptor = ArgumentCaptor.forClass(UsuarioEntity.class);

        verify(usuarioEntityMapper).toEntity(modelCaptor.capture());
        verify(usuarioRepository).save(entityCaptor.capture());
        verify(usuarioEntityMapper).toModel(entityConId);

        assertSame(input, modelCaptor.getValue(), "toEntity debe recibir el modelo de entrada sin mutar");
        assertSame(entitySinId, entityCaptor.getValue(), "repository.save debe recibir la entidad producida por el mapper");
    }

    // ── existsByCorreo ────────────────────────────────────────────────────────

    @Test
    void existsByCorreo_returnsTrue_whenRepositoryReturnsTrue() {
        when(usuarioRepository.existsByCorreo("ana@test.com")).thenReturn(true);
        assertTrue(adapter.existsByCorreo("ana@test.com"));
        verify(usuarioRepository).existsByCorreo("ana@test.com");
    }

    @Test
    void existsByCorreo_returnsFalse_whenRepositoryReturnsFalse() {
        when(usuarioRepository.existsByCorreo("nuevo@test.com")).thenReturn(false);
        assertFalse(adapter.existsByCorreo("nuevo@test.com"));
    }

    // ── existsByDocumentoIdentidad ────────────────────────────────────────────

    @Test
    void existsByDocumentoIdentidad_delegates() {
        when(usuarioRepository.existsByDocumentoIdentidad("123456")).thenReturn(true);
        assertTrue(adapter.existsByDocumentoIdentidad("123456"));
        verify(usuarioRepository).existsByDocumentoIdentidad("123456");
    }

    // ── findByCorreo ──────────────────────────────────────────────────────────

    @Test
    void findByCorreo_whenPresent_mapsEntityToModel() {
        UsuarioEntity entity = stubEntity(DB_ASSIGNED_ID);
        UsuarioModel model = buildInputModel().toBuilder().id(DB_ASSIGNED_ID).build();

        when(usuarioRepository.findByCorreo("ana@test.com")).thenReturn(Optional.of(entity));
        when(usuarioEntityMapper.toModel(entity)).thenReturn(model);

        Optional<UsuarioModel> result = adapter.findByCorreo("ana@test.com");

        assertTrue(result.isPresent());
        assertEquals(DB_ASSIGNED_ID, result.get().getId());
        verify(usuarioEntityMapper).toModel(entity);
    }

    @Test
    void findByCorreo_whenAbsent_returnsEmptyOptional() {
        when(usuarioRepository.findByCorreo("noexiste@test.com")).thenReturn(Optional.empty());

        Optional<UsuarioModel> result = adapter.findByCorreo("noexiste@test.com");

        assertTrue(result.isEmpty());
        verify(usuarioEntityMapper, never()).toModel(any());
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_whenPresent_mapsEntityToModel() {
        UsuarioEntity entity = stubEntity(DB_ASSIGNED_ID);
        UsuarioModel model = buildInputModel().toBuilder().id(DB_ASSIGNED_ID).build();

        when(usuarioRepository.findById(DB_ASSIGNED_ID)).thenReturn(Optional.of(entity));
        when(usuarioEntityMapper.toModel(entity)).thenReturn(model);

        Optional<UsuarioModel> result = adapter.findById(DB_ASSIGNED_ID);

        assertTrue(result.isPresent());
        assertEquals(DB_ASSIGNED_ID, result.get().getId());
    }

    @Test
    void findById_whenAbsent_returnsEmptyOptional() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UsuarioModel> result = adapter.findById(999L);

        assertTrue(result.isEmpty());
        verify(usuarioEntityMapper, never()).toModel(any());
    }
}
