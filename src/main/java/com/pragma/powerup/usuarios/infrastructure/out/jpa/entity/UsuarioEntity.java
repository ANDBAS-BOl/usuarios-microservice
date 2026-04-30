package com.pragma.powerup.usuarios.infrastructure.out.jpa.entity;

import com.pragma.powerup.usuarios.domain.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(
        name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo"),
                @UniqueConstraint(name = "uk_usuario_documento", columnNames = "documento_identidad")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String apellido;

    @Column(name = "documento_identidad", nullable = false, length = 32)
    private String documentoIdentidad;

    @Column(nullable = false, length = 13)
    private String celular;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 255)
    private String correo;

    @Column(nullable = false, length = 255)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Rol rol;
}
