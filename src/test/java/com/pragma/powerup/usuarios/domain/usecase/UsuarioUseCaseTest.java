package com.pragma.powerup.usuarios.domain.usecase;

import com.pragma.powerup.usuarios.domain.exception.BusinessRuleException;
import com.pragma.powerup.usuarios.domain.exception.ConflictoException;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import com.pragma.powerup.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.usuarios.domain.spi.IUsuarioPersistencePort;
import com.pragma.powerup.usuarios.domain.utils.DomainErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private IUsuarioPersistencePort persistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    private UsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UsuarioUseCase(persistencePort, passwordEncoderPort);
    }

    // ── HU 8 — registrarCliente ───────────────────────────────────────────────

    @Test
    void registrarCliente_persisteConRolClienteYClaveEncriptada() {
        when(persistencePort.existsByCorreo("c@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("888")).thenReturn(false);
        when(passwordEncoderPort.encode("password12")).thenReturn("HASHED_password12");
        when(persistencePort.save(any(UsuarioModel.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("888")
                .celular("+573001112233")
                .correo("c@test.com")
                .build();

        ArgumentCaptor<UsuarioModel> captor = ArgumentCaptor.forClass(UsuarioModel.class);
        useCase.registrarCliente(model, "password12");
        verify(persistencePort).save(captor.capture());
        UsuarioModel saved = captor.getValue();

        assertThat(saved.getRol()).isEqualTo(Rol.CLIENTE);
        assertThat(saved.getClaveEncriptada()).isEqualTo("HASHED_password12");
        assertThat(saved.getFechaNacimiento()).isNull();
    }

    @Test
    void registrarCliente_correoDuplicado_lanzaConflictoConMensaje() {
        when(persistencePort.existsByCorreo("c@test.com")).thenReturn(true);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("999")
                .celular("+573001112233")
                .correo("c@test.com")
                .build();

        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> useCase.registrarCliente(model, "password12"));
        assertEquals(DomainErrorMessage.CORREO_YA_REGISTRADO.getMessage(), ex.getMessage());
    }

    @Test
    void registrarCliente_documentoDuplicado_lanzaConflictoConMensaje() {
        when(persistencePort.existsByCorreo("c2@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("999")).thenReturn(true);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("999")
                .celular("+573001112233")
                .correo("c2@test.com")
                .build();

        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> useCase.registrarCliente(model, "password12"));
        assertEquals(DomainErrorMessage.DOCUMENTO_YA_REGISTRADO.getMessage(), ex.getMessage());
    }

    // ── HU 1 — registrarPropietario ───────────────────────────────────────────

    @Test
    void registrarPropietario_happyPath_persisteConRolPropietarioYClaveEncriptada() {
        when(persistencePort.existsByCorreo("owner@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("777")).thenReturn(false);
        when(passwordEncoderPort.encode("OwnerPass1")).thenReturn("HASHED_OwnerPass1");
        when(persistencePort.save(any(UsuarioModel.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioModel model = UsuarioModel.builder()
                .nombre("Dueno")
                .apellido("Test")
                .documentoIdentidad("777")
                .celular("+573009998877")
                .correo("owner@test.com")
                .fechaNacimiento(LocalDate.of(1985, 5, 20))
                .build();

        ArgumentCaptor<UsuarioModel> captor = ArgumentCaptor.forClass(UsuarioModel.class);
        useCase.registrarPropietario(model, "OwnerPass1");
        verify(persistencePort).save(captor.capture());
        UsuarioModel saved = captor.getValue();

        assertThat(saved.getRol()).isEqualTo(Rol.PROPIETARIO);
        assertThat(saved.getClaveEncriptada()).isEqualTo("HASHED_OwnerPass1");
        assertThat(saved.getFechaNacimiento()).isEqualTo(LocalDate.of(1985, 5, 20));
    }

    @Test
    void registrarPropietario_menorDeEdad_lanzaBusinessRuleConMensaje() {
        when(persistencePort.existsByCorreo("p@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("123")).thenReturn(false);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("123")
                .celular("+573001112233")
                .correo("p@test.com")
                .fechaNacimiento(LocalDate.now().minusYears(17))
                .build();

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> useCase.registrarPropietario(model, "password12"));
        assertEquals(DomainErrorMessage.PROPIETARIO_MENOR_DE_EDAD.getMessage(), ex.getMessage());
    }

    @Test
    void registrarPropietario_sinFechaNacimiento_lanzaBusinessRuleConMensaje() {
        when(persistencePort.existsByCorreo("no-birth@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("555")).thenReturn(false);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("555")
                .celular("+573001112233")
                .correo("no-birth@test.com")
                .fechaNacimiento(null)
                .build();

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> useCase.registrarPropietario(model, "pass"));
        assertEquals(DomainErrorMessage.FECHA_NACIMIENTO_REQUERIDA.getMessage(), ex.getMessage());
    }

    @Test
    void registrarPropietario_documentoDuplicado_lanzaConflictoConMensaje() {
        when(persistencePort.existsByCorreo("owner2@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("777")).thenReturn(true);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("777")
                .celular("+573001112233")
                .correo("owner2@test.com")
                .fechaNacimiento(LocalDate.of(1985, 5, 20))
                .build();

        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> useCase.registrarPropietario(model, "pass"));
        assertEquals(DomainErrorMessage.DOCUMENTO_YA_REGISTRADO.getMessage(), ex.getMessage());
    }

    // ── HU 6 — registrarEmpleado ──────────────────────────────────────────────

    @Test
    void registrarEmpleado_happyPath_persisteConRolEmpleadoYClaveEncriptada() {
        when(persistencePort.existsByCorreo("emp@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("666")).thenReturn(false);
        when(passwordEncoderPort.encode("EmpPass1")).thenReturn("HASHED_EmpPass1");
        when(persistencePort.save(any(UsuarioModel.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioModel model = UsuarioModel.builder()
                .nombre("Emp")
                .apellido("Test")
                .documentoIdentidad("666")
                .celular("+573001234567")
                .correo("emp@test.com")
                .build();

        ArgumentCaptor<UsuarioModel> captor = ArgumentCaptor.forClass(UsuarioModel.class);
        useCase.registrarEmpleado(model, "EmpPass1");
        verify(persistencePort).save(captor.capture());
        UsuarioModel saved = captor.getValue();

        assertThat(saved.getRol()).isEqualTo(Rol.EMPLEADO);
        assertThat(saved.getClaveEncriptada()).isEqualTo("HASHED_EmpPass1");
        assertThat(saved.getFechaNacimiento()).isNull();
    }

    @Test
    void registrarEmpleado_correoDuplicado_lanzaConflictoConMensaje() {
        when(persistencePort.existsByCorreo("emp@test.com")).thenReturn(true);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("Emp")
                .apellido("Test")
                .documentoIdentidad("666")
                .celular("+573001234567")
                .correo("emp@test.com")
                .build();

        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> useCase.registrarEmpleado(model, "pass"));
        assertEquals(DomainErrorMessage.CORREO_YA_REGISTRADO.getMessage(), ex.getMessage());
    }

    @Test
    void buscarPorId_delegaAlPersistencePort() {
        useCase.buscarPorId(42L);
        verify(persistencePort).findById(42L);
    }
}
