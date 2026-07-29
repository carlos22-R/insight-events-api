package com.insightevents.events_api.domain.enums;

/**
 * Ciclo de vida de un evento:
 * NUEVO -> EN_INVESTIGACION -> RESUELTO -> CERRADO
 * (o DESCARTADO si resulto ser una falsa alarma).
 */
public enum EstadoEvento {
    NUEVO,
    EN_INVESTIGACION,
    RESUELTO,
    CERRADO,
    DESCARTADO
}
