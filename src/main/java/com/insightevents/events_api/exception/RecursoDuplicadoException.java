package com.insightevents.events_api.exception;

/**
 * Se lanza cuando se intenta crear/actualizar un recurso que viola una
 * regla de unicidad (por ejemplo, categoria con nombre repetido).
 * El manejador global la traduce a HTTP 409 (Conflict).
 */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
