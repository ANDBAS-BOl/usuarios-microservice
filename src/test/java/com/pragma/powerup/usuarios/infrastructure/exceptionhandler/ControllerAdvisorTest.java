package com.pragma.powerup.usuarios.infrastructure.exceptionhandler;

import com.pragma.powerup.usuarios.domain.exception.AccessDeniedException;
import com.pragma.powerup.usuarios.domain.exception.BusinessRuleException;
import com.pragma.powerup.usuarios.domain.exception.ConflictoException;
import com.pragma.powerup.usuarios.domain.exception.CredencialesInvalidasException;
import com.pragma.powerup.usuarios.domain.exception.DomainException;
import com.pragma.powerup.usuarios.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControllerAdvisorTest {

    private final ControllerAdvisor advisor = new ControllerAdvisor();

    // ── Domain exceptions ─────────────────────────────────────────────────────

    @Test
    void handleBusinessRule_returns400WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleBusinessRule(new BusinessRuleException("regla de negocio"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("regla de negocio", response.getBody().get("message"));
    }

    @Test
    void handleNotFound_returns404WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleNotFound(new ResourceNotFoundException("usuario no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("usuario no encontrado", response.getBody().get("message"));
    }

    @Test
    void handleAccessDenied_returns403WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleAccessDenied(new AccessDeniedException("acceso denegado"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("acceso denegado", response.getBody().get("message"));
    }

    @Test
    void handleConflicto_returns409WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleConflicto(new ConflictoException("conflicto de datos"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("conflicto de datos", response.getBody().get("message"));
    }

    @Test
    void handleCredencialesInvalidas_returns401WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleCredencialesInvalidas(new CredencialesInvalidasException("credenciales invalidas"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("credenciales invalidas", response.getBody().get("message"));
    }

    @Test
    void handleDomainException_fallback_returns400WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleDomainException(new TestDomainException("excepcion generica de dominio"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("excepcion generica de dominio", response.getBody().get("message"));
    }

    // ── Infrastructure exceptions ─────────────────────────────────────────────

    @Test
    void handleDataIntegrity_returns409WithGenericMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleDataIntegrity(new DataIntegrityViolationException("duplicate key"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody().get("message"));
    }

    @Test
    void handleNotReadable_returns400WithGenericMessage() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("bad json", new MockHttpInputMessage(new byte[0]));

        ResponseEntity<Map<String, String>> response = advisor.handleNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().get("message"));
    }

    // ── Bean Validation ───────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void handleValidation_returns400WithFieldErrorsMap() throws Exception {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "nombre", "no debe estar en blanco"));
        bindingResult.addError(new FieldError("request", "correo", "no debe estar en blanco"));

        java.lang.reflect.Method dummy = DummyController.class.getDeclaredMethod("dummy", String.class);
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(
                        new org.springframework.core.MethodParameter(dummy, 0), bindingResult);

        ResponseEntity<Map<String, Object>> response = advisor.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().get("message"));
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertTrue(errors.containsKey("nombre"));
        assertTrue(errors.containsKey("correo"));
        assertEquals("no debe estar en blanco", errors.get("nombre"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static class TestDomainException extends DomainException {
        TestDomainException(String message) {
            super(message);
        }
    }

    private static class DummyController {
        @SuppressWarnings("unused")
        public void dummy(String body) {
        }
    }
}
