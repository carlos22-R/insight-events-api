package com.insightevents.events_api.mapper;

import com.insightevents.events_api.domain.Asignacion;
import com.insightevents.events_api.dto.AsignacionResponse;
import org.springframework.stereotype.Component;

/**
 * Convierte la entidad Asignacion a su DTO de respuesta.
 */
@Component
public class AsignacionMapper {

    public AsignacionResponse toResponse(Asignacion a) {
        return new AsignacionResponse(
                a.getId(),
                a.getEvento().getId(),
                a.getEvento().getCodigo(),
                a.getAnalista().getId(),
                a.getAnalista().getNombre(),
                a.getFechaAsignacion(),
                a.getEstado());
    }
}
