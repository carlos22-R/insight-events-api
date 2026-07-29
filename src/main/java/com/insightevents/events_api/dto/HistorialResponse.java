package com.insightevents.events_api.dto;

import com.insightevents.events_api.domain.enums.TipoAccion;
import java.time.LocalDateTime;

/**
 * Una entrada del historial de un evento.
 */
public record HistorialResponse(
        Long id,
        String usuario,
        LocalDateTime fecha,
        TipoAccion accion,
        String comentario
) {}
