package com.insightevents.events_api.service;

import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import com.insightevents.events_api.dto.EventoActualizarRequest;
import com.insightevents.events_api.dto.EventoCrearRequest;
import com.insightevents.events_api.dto.EventoResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

/**
 * Contrato del servicio de eventos.
 */
public interface EventoService {

    EventoResponse crear(EventoCrearRequest request, String usuario);

    EventoResponse actualizar(Long id, EventoActualizarRequest request, String usuario);

    EventoResponse obtenerPorId(Long id);

    /** Busqueda con filtros opcionales y paginacion (JPQL). */
    PaginaResponse<EventoResponse> buscar(Long categoriaId, EstadoEvento estado,
                                          Prioridad prioridad, LocalDateTime fechaDesde,
                                          LocalDateTime fechaHasta, String texto,
                                          Pageable pageable);

    /** Borrado logico (soft delete): marca activo = false. */
    void eliminar(Long id, String usuario);
}
