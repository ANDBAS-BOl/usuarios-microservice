package com.pragma.powerup.usuarios.infrastructure.input.rest;

import com.pragma.powerup.usuarios.application.dto.request.RegistroClienteRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroEmpleadoRequestDto;
import com.pragma.powerup.usuarios.application.dto.request.RegistroPropietarioRequestDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioRoleEmpleadoValidationResponseDto;
import com.pragma.powerup.usuarios.application.dto.response.UsuarioCreadoResponseDto;
import com.pragma.powerup.usuarios.application.handler.IUsuarioHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Usuarios", description = "Registro y gestión de identidad (HU 1, 6, 8)")
public class UsuarioRestController {

    private final IUsuarioHandler usuarioHandler;

    @Operation(summary = "Registro público de cliente (HU 8)")
    @PostMapping("/registro/cliente")
    public ResponseEntity<UsuarioCreadoResponseDto> registrarCliente(
            @Valid @RequestBody RegistroClienteRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioHandler.registrarCliente(dto));
    }

    @Operation(summary = "Crear propietario (HU 1)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/propietarios")
    public ResponseEntity<UsuarioCreadoResponseDto> registrarPropietario(
            @Valid @RequestBody RegistroPropietarioRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioHandler.registrarPropietario(dto));
    }

    @Operation(summary = "Crear empleado (HU 6)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PostMapping("/empleados")
    public ResponseEntity<UsuarioCreadoResponseDto> registrarEmpleado(
            @Valid @RequestBody RegistroEmpleadoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioHandler.registrarEmpleado(dto));
    }

    @Operation(summary = "Validar si un usuario existe y tiene rol PROPIETARIO", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{idUsuario}/validacion-propietario")
    public ResponseEntity<UsuarioRoleValidationResponseDto> validarPropietario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(usuarioHandler.validarUsuarioPropietario(idUsuario));
    }

    @Operation(summary = "Validar si un usuario existe y tiene rol EMPLEADO", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROPIETARIO')")
    @GetMapping("/{idUsuario}/validacion-empleado")
    public ResponseEntity<UsuarioRoleEmpleadoValidationResponseDto> validarEmpleado(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(usuarioHandler.validarUsuarioEmpleado(idUsuario));
    }
}
