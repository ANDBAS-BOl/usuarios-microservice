package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.ConflictoException;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Rol;
import com.pragma.powerup.domain.model.UsuarioModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private IUsuarioPersistencePort persistencePort;

    private PasswordEncoder passwordEncoder;
    private IPasswordEncoderPort passwordEncoderPort;
    private UsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        passwordEncoderPort = passwordEncoder::encode;
        useCase = new UsuarioUseCase(persistencePort, passwordEncoderPort);
    }

    @Test
    void registrarPropietario_menorDeEdad_lanzaDomainException() {
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

        assertThatThrownBy(() -> useCase.registrarPropietario(model, "password12"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("mayor de edad");
    }

    @Test
    void registrarCliente_correoDuplicado_lanzaConflicto() {
        when(persistencePort.existsByCorreo("c@test.com")).thenReturn(true);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("A")
                .apellido("B")
                .documentoIdentidad("999")
                .celular("+573001112233")
                .correo("c@test.com")
                .build();

        assertThatThrownBy(() -> useCase.registrarCliente(model, "password12"))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    void registrarCliente_persisteConRolClienteYClaveEncriptada() {
        when(persistencePort.existsByCorreo("c@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("888")).thenReturn(false);
        when(persistencePort.save(any(UsuarioModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        assertThat(passwordEncoder.matches("password12", saved.getClaveEncriptada())).isTrue();
    }

    // ========================
    // HU 1 — registrarPropietario
    // ========================

    @Test
    void registrarPropietario_happyPath_persisteConRolPropietarioYClaveEncriptada() {
        when(persistencePort.existsByCorreo("owner@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("777")).thenReturn(false);
        when(persistencePort.save(any(UsuarioModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        assertThat(passwordEncoder.matches("OwnerPass1", saved.getClaveEncriptada())).isTrue();
        assertThat(saved.getFechaNacimiento()).isEqualTo(LocalDate.of(1985, 5, 20));
    }

    @Test
    void registrarPropietario_sinFechaNacimiento_lanzaDomainException() {
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

        assertThatThrownBy(() -> useCase.registrarPropietario(model, "pass"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("fecha de nacimiento");
    }

    @Test
    void registrarPropietario_documentoDuplicado_lanzaConflicto() {
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

        assertThatThrownBy(() -> useCase.registrarPropietario(model, "pass"))
                .isInstanceOf(ConflictoException.class);
    }

    // ========================
    // HU 6 — registrarEmpleado
    // ========================

    @Test
    void registrarEmpleado_happyPath_persisteConRolEmpleadoYClaveEncriptada() {
        when(persistencePort.existsByCorreo("emp@test.com")).thenReturn(false);
        when(persistencePort.existsByDocumentoIdentidad("666")).thenReturn(false);
        when(persistencePort.save(any(UsuarioModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        assertThat(passwordEncoder.matches("EmpPass1", saved.getClaveEncriptada())).isTrue();
        assertThat(saved.getFechaNacimiento()).isNull();
    }

    @Test
    void registrarEmpleado_correoDuplicado_lanzaConflicto() {
        when(persistencePort.existsByCorreo("emp@test.com")).thenReturn(true);

        UsuarioModel model = UsuarioModel.builder()
                .nombre("Emp")
                .apellido("Test")
                .documentoIdentidad("666")
                .celular("+573001234567")
                .correo("emp@test.com")
                .build();

        assertThatThrownBy(() -> useCase.registrarEmpleado(model, "pass"))
                .isInstanceOf(ConflictoException.class);
    }
}
