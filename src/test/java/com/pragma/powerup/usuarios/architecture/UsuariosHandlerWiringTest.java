package com.pragma.powerup.usuarios.architecture;

import com.pragma.powerup.usuarios.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleEmpleadoValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleValidationResponseDto;
import com.pragma.powerup.usuarios.application.handler.IAuthPrincipal;
import com.pragma.powerup.usuarios.application.handler.impl.AuthHandler;
import com.pragma.powerup.usuarios.application.handler.impl.UsuarioHandler;
import com.pragma.powerup.usuarios.application.mapper.IUsuarioDtoMapper;
import com.pragma.powerup.usuarios.domain.api.IUsuarioServicePort;
import com.pragma.powerup.usuarios.domain.exception.CredencialesInvalidasException;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.domain.model.UsuarioModel;
import com.pragma.powerup.usuarios.domain.spi.ITokenProviderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuariosHandlerWiringTest {

    // ── UsuarioHandler ────────────────────────────────────────────────────────
    @Mock
    private IUsuarioServicePort usuarioServicePort;
    @Mock
    private IUsuarioDtoMapper usuarioDtoMapper;
    @InjectMocks
    private UsuarioHandler usuarioHandler;

    // ── AuthHandler ───────────────────────────────────────────────────────────
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ITokenProviderPort tokenProviderPort;
    @InjectMocks
    private AuthHandler authHandler;

    // ── helpers ───────────────────────────────────────────────────────────────

    private UsuarioModel clienteModel() {
        return UsuarioModel.builder()
                .nombre("Ana")
                .apellido("Lopez")
                .documentoIdentidad("11111111")
                .celular("3001234567")
                .correo("ana@test.com")
                .claveEncriptada("HASHED")
                .rol(Rol.CLIENTE)
                .build();
    }

    private UsuarioModel propietarioModel() {
        return UsuarioModel.builder()
                .nombre("Carlos")
                .apellido("Ruiz")
                .documentoIdentidad("22222222")
                .celular("3119876543")
                .correo("carlos@test.com")
                .claveEncriptada("HASHED")
                .fechaNacimiento(LocalDate.of(1985, 3, 15))
                .rol(Rol.PROPIETARIO)
                .build();
    }

    private UsuarioModel empleadoModel() {
        return UsuarioModel.builder()
                .nombre("Pedro")
                .apellido("Gomez")
                .documentoIdentidad("33333333")
                .celular("3201112233")
                .correo("pedro@test.com")
                .claveEncriptada("HASHED")
                .rol(Rol.EMPLEADO)
                .build();
    }

    private RegistroClienteRequestDto clienteDto() {
        RegistroClienteRequestDto dto = new RegistroClienteRequestDto();
        dto.setNombre("Ana");
        dto.setApellido("Lopez");
        dto.setDocumentoIdentidad("11111111");
        dto.setCelular("3001234567");
        dto.setCorreo("ana@test.com");
        dto.setClave("clave123");
        return dto;
    }

    private RegistroPropietarioRequestDto propietarioDto() {
        RegistroPropietarioRequestDto dto = new RegistroPropietarioRequestDto();
        dto.setNombre("Carlos");
        dto.setApellido("Ruiz");
        dto.setDocumentoIdentidad("22222222");
        dto.setCelular("3119876543");
        dto.setCorreo("carlos@test.com");
        dto.setFechaNacimiento(LocalDate.of(1985, 3, 15));
        dto.setClave("clave456");
        return dto;
    }

    private RegistroEmpleadoRequestDto empleadoDto() {
        RegistroEmpleadoRequestDto dto = new RegistroEmpleadoRequestDto();
        dto.setNombre("Pedro");
        dto.setApellido("Gomez");
        dto.setDocumentoIdentidad("33333333");
        dto.setCelular("3201112233");
        dto.setCorreo("pedro@test.com");
        dto.setClave("clave789");
        return dto;
    }

    // ── UsuarioHandler tests ──────────────────────────────────────────────────

    @Test
    void shouldDelegateClienteRegistrationToServicePort() {
        RegistroClienteRequestDto dto = clienteDto();
        UsuarioModel model = clienteModel();
        UsuarioCreadoResponseDto expected = UsuarioCreadoResponseDto.builder()
                .id(1L).correo("ana@test.com").rol("CLIENTE").build();

        when(usuarioDtoMapper.toModel(dto)).thenReturn(model);
        when(usuarioServicePort.registrarCliente(model, dto.getClave())).thenReturn(model);
        when(usuarioDtoMapper.toCreadoResponse(model)).thenReturn(expected);

        UsuarioCreadoResponseDto result = usuarioHandler.registrarCliente(dto);

        assertEquals(expected, result);
        verify(usuarioDtoMapper).toModel(dto);
        verify(usuarioServicePort).registrarCliente(model, "clave123");
        verify(usuarioDtoMapper).toCreadoResponse(model);
    }

    @Test
    void shouldDelegatePropietarioRegistrationToServicePort() {
        RegistroPropietarioRequestDto dto = propietarioDto();
        UsuarioModel model = propietarioModel();
        UsuarioCreadoResponseDto expected = UsuarioCreadoResponseDto.builder()
                .id(2L).correo("carlos@test.com").rol("PROPIETARIO").build();

        when(usuarioDtoMapper.toModel(dto)).thenReturn(model);
        when(usuarioServicePort.registrarPropietario(model, dto.getClave())).thenReturn(model);
        when(usuarioDtoMapper.toCreadoResponse(model)).thenReturn(expected);

        UsuarioCreadoResponseDto result = usuarioHandler.registrarPropietario(dto);

        assertEquals(expected, result);
        verify(usuarioDtoMapper).toModel(dto);
        verify(usuarioServicePort).registrarPropietario(model, "clave456");
        verify(usuarioDtoMapper).toCreadoResponse(model);
    }

    @Test
    void shouldDelegateEmpleadoRegistrationToServicePort() {
        RegistroEmpleadoRequestDto dto = empleadoDto();
        UsuarioModel model = empleadoModel();
        UsuarioCreadoResponseDto expected = UsuarioCreadoResponseDto.builder()
                .id(3L).correo("pedro@test.com").rol("EMPLEADO").build();

        when(usuarioDtoMapper.toModel(dto)).thenReturn(model);
        when(usuarioServicePort.registrarEmpleado(model, dto.getClave())).thenReturn(model);
        when(usuarioDtoMapper.toCreadoResponse(model)).thenReturn(expected);

        UsuarioCreadoResponseDto result = usuarioHandler.registrarEmpleado(dto);

        assertEquals(expected, result);
        verify(usuarioDtoMapper).toModel(dto);
        verify(usuarioServicePort).registrarEmpleado(model, "clave789");
        verify(usuarioDtoMapper).toCreadoResponse(model);
    }

    @Test
    void shouldValidarPropietarioReturnTrueWhenRolIsPropietario() {
        UsuarioModel model = propietarioModel();
        when(usuarioServicePort.buscarPorId(2L)).thenReturn(Optional.of(model));

        UsuarioRoleValidationResponseDto result = usuarioHandler.validarUsuarioPropietario(2L);

        assertTrue(result.propietarioValido());
        assertEquals("PROPIETARIO", result.rol());
        verify(usuarioServicePort).buscarPorId(2L);
    }

    @Test
    void shouldValidarPropietarioReturnFalseWhenRolIsNotPropietario() {
        UsuarioModel model = clienteModel();
        when(usuarioServicePort.buscarPorId(1L)).thenReturn(Optional.of(model));

        UsuarioRoleValidationResponseDto result = usuarioHandler.validarUsuarioPropietario(1L);

        assertFalse(result.propietarioValido());
        assertEquals("CLIENTE", result.rol());
    }

    @Test
    void shouldValidarEmpleadoReturnTrueWhenRolIsEmpleado() {
        UsuarioModel model = empleadoModel();
        when(usuarioServicePort.buscarPorId(3L)).thenReturn(Optional.of(model));

        UsuarioRoleEmpleadoValidationResponseDto result = usuarioHandler.validarUsuarioEmpleado(3L);

        assertTrue(result.empleadoValido());
        assertEquals("EMPLEADO", result.rol());
        verify(usuarioServicePort).buscarPorId(3L);
    }

    @Test
    void shouldValidarEmpleadoReturnFalseWhenRolIsNotEmpleado() {
        UsuarioModel model = propietarioModel();
        when(usuarioServicePort.buscarPorId(2L)).thenReturn(Optional.of(model));

        UsuarioRoleEmpleadoValidationResponseDto result = usuarioHandler.validarUsuarioEmpleado(2L);

        assertFalse(result.empleadoValido());
        assertEquals("PROPIETARIO", result.rol());
    }

    // ── AuthHandler tests ─────────────────────────────────────────────────────

    @Test
    void shouldDelegateLoginToAuthManagerAndGenerateToken() {
        IAuthPrincipal principal = mock(IAuthPrincipal.class);
        when(principal.getId()).thenReturn(10L);
        when(principal.getUsername()).thenReturn("ana@test.com");
        when(principal.getRolName()).thenReturn("CLIENTE");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProviderPort.generateToken(10L, "ana@test.com", "CLIENTE")).thenReturn("JWT_TOKEN");

        com.pragma.powerup.usuarios.application.dto.request.LoginRequestDto dto =
                new com.pragma.powerup.usuarios.application.dto.request.LoginRequestDto();
        dto.setCorreo("ana@test.com");
        dto.setClave("clave123");

        com.pragma.powerup.usuarios.application.dto.response.LoginResponseDto result = authHandler.login(dto);

        assertEquals("JWT_TOKEN", result.getToken());
        assertEquals(10L, result.getIdUsuario());
        assertEquals("CLIENTE", result.getRol());
        verify(authenticationManager).authenticate(any());
        verify(tokenProviderPort).generateToken(10L, "ana@test.com", "CLIENTE");
    }

    @Test
    void shouldThrowCredencialesInvalidasOnBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        com.pragma.powerup.usuarios.application.dto.request.LoginRequestDto dto =
                new com.pragma.powerup.usuarios.application.dto.request.LoginRequestDto();
        dto.setCorreo("bad@test.com");
        dto.setClave("wrong");

        assertThrows(CredencialesInvalidasException.class, () -> authHandler.login(dto));
    }
}
