package com.insightevents.events_api.service;

import com.insightevents.events_api.dto.AnalistaRequest;
import com.insightevents.events_api.dto.AnalistaResponse;
import com.insightevents.events_api.dto.EventoAsignadoResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Contrato del servicio de analistas.
 */
public interface AnalistaService {

    AnalistaResponse crear(AnalistaRequest request);

    AnalistaResponse actualizar(Long id, AnalistaRequest request);

    AnalistaResponse obtenerPorId(Long id);

    List<AnalistaResponse> listar();

    void eliminar(Long id);

    /** Eventos activos asignados a un analista (con id de asignacion), paginados. */
    PaginaResponse<EventoAsignadoResponse> eventosAsignados(Long analistaId, Pageable pageable);
}
