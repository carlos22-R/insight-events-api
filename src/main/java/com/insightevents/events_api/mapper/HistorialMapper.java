package com.insightevents.events_api.mapper;

import com.insightevents.events_api.domain.HistorialEvento;
import com.insightevents.events_api.dto.HistorialResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Convierte entradas de historial a sus DTOs de respuesta.
 */
@Component
public class HistorialMapper {

    public HistorialResponse toResponse(HistorialEvento h) {
        return new HistorialResponse(
                h.getId(),
                h.getUsuario(),
                h.getFecha(),
                h.getAccion(),
                h.getComentario());
    }

    public List<HistorialResponse> toResponseList(List<HistorialEvento> historial) {
        return historial.stream().map(this::toResponse).toList();
    }
}
