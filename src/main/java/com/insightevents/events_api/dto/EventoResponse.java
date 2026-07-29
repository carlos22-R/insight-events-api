package com.insightevents.events_api.dto;

import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import java.time.LocalDateTime;

/**
 * Datos de un evento que la API devuelve. La categoria se aplana a id + nombre
 * para no exponer la entidad completa.
 */
public record EventoResponse(
        Long id,
        String codigo,
        String titulo,
        String descripcion,
        LocalDateTime fecha,
        Prioridad prioridad,
        EstadoEvento estado,
        String fuente,
        Long categoriaId,
        String categoriaNombre,
        boolean activo
) {}
