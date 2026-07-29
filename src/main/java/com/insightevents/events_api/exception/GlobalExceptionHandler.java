package com.insightevents.events_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejador global de errores. Centraliza en un solo lugar la traduccion
 * de excepciones a respuestas HTTP consistentes, para que ningun controller
 * tenga que hacer try/catch. Todas las respuestas usan el formato {@link ApiError}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Recurso inexistente -> 404 Not Found. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        ApiError body = ApiError.de(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /** Violacion de unicidad de negocio -> 409 Conflict. */
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ApiError> duplicado(RecursoDuplicadoException ex, HttpServletRequest req) {
        ApiError body = ApiError.de(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /** Recurso con dependientes (no se puede borrar) -> 409 Conflict. */
    @ExceptionHandler(RecursoEnUsoException.class)
    public ResponseEntity<ApiError> enUso(RecursoEnUsoException ex, HttpServletRequest req) {
        ApiError body = ApiError.de(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Red de seguridad: cualquier violacion de integridad de la BD (FK, unique)
     * que se escape de las validaciones de negocio -> 409 en vez de un 500.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        ApiError body = ApiError.de(HttpStatus.CONFLICT.value(), "Conflict",
                "La operacion viola una restriccion de integridad de datos", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /** Parametro con tipo invalido (p.ej. un enum mal escrito en la URL) -> 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = "Valor invalido '%s' para el parametro '%s'".formatted(ex.getValue(), ex.getName());
        ApiError body = ApiError.de(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg, req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    /** Fallo de validacion de Bean Validation (@Valid) -> 400 Bad Request. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errores.put(fe.getField(), fe.getDefaultMessage()));

        ApiError body = new ApiError(
                java.time.LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Error de validacion en los datos enviados",
                req.getRequestURI(),
                errores);
        return ResponseEntity.badRequest().body(body);
    }
}
