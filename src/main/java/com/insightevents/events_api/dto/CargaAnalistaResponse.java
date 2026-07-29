package com.insightevents.events_api.dto;

/**
 * Reporte de carga de trabajo: cuantas asignaciones activas tiene un analista.
 */
public record CargaAnalistaResponse(
        Long analistaId,
        String analistaNombre,
        long totalAsignaciones
) {}
