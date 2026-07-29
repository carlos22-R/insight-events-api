package com.insightevents.events_api.exception;

/**
 * Se lanza al intentar eliminar un recurso que tiene otros dependiendo de el
 * (por ejemplo, una categoria con eventos asociados).
 * El manejador global la traduce a HTTP 409 (Conflict).
 */
public class RecursoEnUsoException extends RuntimeException {

    public RecursoEnUsoException(String mensaje) {
        super(mensaje);
    }
}
