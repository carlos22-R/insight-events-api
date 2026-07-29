package com.insightevents.events_api.mapper;

import com.insightevents.events_api.domain.Asignacion;
import com.insightevents.events_api.domain.Categoria;
import com.insightevents.events_api.domain.Evento;
import com.insightevents.events_api.dto.AsignacionResponse;
import com.insightevents.events_api.dto.EventoAsignadoResponse;
import org.springframework.stereotype.Component;

/**
 * Convierte la entidad Asignacion a sus DTOs de respuesta.
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

    /** Vista de "evento asignado": datos del evento + id/estado de la asignacion. */
    public EventoAsignadoResponse toEventoAsignado(Asignacion a) {
        Evento e = a.getEvento();
        Categoria c = e.getCategoria();
        return new EventoAsignadoResponse(
                a.getId(),
                a.getEstado(),
                a.getFechaAsignacion(),
                e.getId(),
                e.getCodigo(),
                e.getTitulo(),
                e.getPrioridad(),
                e.getEstado(),
                c != null ? c.getNombre() : null);
    }
}
