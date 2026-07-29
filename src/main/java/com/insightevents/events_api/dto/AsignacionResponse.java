package com.insightevents.events_api.dto;

import com.insightevents.events_api.domain.enums.EstadoAsignacion;
import java.time.LocalDateTime;

/**
 * Datos de una asignacion que la API devuelve.
 */
public record AsignacionResponse(
        Long id,
        Long eventoId,
        String eventoCodigo,
        Long analistaId,
        String analistaNombre,
        LocalDateTime fechaAsignacion,
        EstadoAsignacion estado
) {}
