package com.insightevents.events_api.domain.enums;

/**
 * Ciclo de vida de un evento:
 * NUEVO -> EN_INVESTIGACION -> RESUELTO
 * (o DESCARTADO si se cancela / resulto ser una falsa alarma).
 *
 * Las transiciones las controlan acciones dedicadas:
 *   crear -> NUEVO | asignar -> EN_INVESTIGACION |
 *   resolver -> RESUELTO | cancelar -> DESCARTADO
 */
public enum EstadoEvento {
    NUEVO,
    EN_INVESTIGACION,
    RESUELTO,
    DESCARTADO
}
