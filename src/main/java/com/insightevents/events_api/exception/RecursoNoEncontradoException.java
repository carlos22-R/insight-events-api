package com.insightevents.events_api.exception;

/**
 * Se lanza cuando se busca un recurso por id y no existe.
 * El manejador global la traduce a HTTP 404 (Not Found).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("%s con id %s no encontrado".formatted(recurso, id));
    }
}
