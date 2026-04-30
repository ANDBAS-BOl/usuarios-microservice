package com.pragma.powerup.usuarios.infrastructure.security;

import com.pragma.powerup.usuarios.application.handler.IAuthPrincipal;
import com.pragma.powerup.usuarios.domain.model.Rol;
import com.pragma.powerup.usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UsuarioPrincipal implements UserDetails, IAuthPrincipal {

    private final Long id;
    private final String correo;
    private final String password;
    private final Rol rol;

    public static UsuarioPrincipal fromEntity(UsuarioEntity entity) {
        return new UsuarioPrincipal(
                entity.getId(),
                entity.getCorreo(),
                entity.getClave(),
                entity.getRol());
    }

    public static UsuarioPrincipal fromTokenClaims(Long id, String correo, Rol rol) {
        return new UsuarioPrincipal(id, correo, "", rol);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getRolName() {
        return rol.name();
    }
}
