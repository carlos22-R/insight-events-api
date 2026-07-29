package com.insightevents.events_api.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cuerpo de error uniforme que devuelve la API ante cualquier fallo.
 * Tener un formato unico hace la API predecible para quien la consume.
 *
 * @param timestamp momento del error
 * @param status    codigo HTTP (404, 409, 400, ...)
 * @param error     nombre corto del error ("Not Found", "Conflict", ...)
 * @param message   mensaje legible de lo que paso
 * @param path      ruta que se estaba llamando
 * @param errores   detalle campo->mensaje (solo en errores de validacion; null si no aplica)
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errores
) {
    public static ApiError de(int status, String error, String message, String path) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, null);
    }
}
