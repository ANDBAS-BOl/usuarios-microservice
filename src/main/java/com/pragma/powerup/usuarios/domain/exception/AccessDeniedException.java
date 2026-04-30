package com.pragma.powerup.usuarios.domain.exception;

public class AccessDeniedException extends DomainException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
