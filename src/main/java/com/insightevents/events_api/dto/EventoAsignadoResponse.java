package com.insightevents.events_api.dto;

import com.insightevents.events_api.domain.enums.EstadoAsignacion;
import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import java.time.LocalDateTime;

/**
 * Un evento asignado a un analista, incluyendo los datos de la asignacion
 * (id y estado) para poder resolverla desde aqui.
 */
public record EventoAsignadoResponse(
        Long asignacionId,
        EstadoAsignacion estadoAsignacion,
        LocalDateTime fechaAsignacion,
        Long eventoId,
        String eventoCodigo,
        String titulo,
        Prioridad prioridad,
        EstadoEvento estadoEvento,
        String categoriaNombre
) {}
