package com.insightevents.events_api.service;

import com.insightevents.events_api.dto.CargaAnalistaResponse;
import com.insightevents.events_api.dto.EventoResponse;
import java.util.List;

/**
 * Contrato del servicio de reportes.
 */
public interface ReporteService {

    /** Carga de trabajo por analista (SQL nativo). */
    List<CargaAnalistaResponse> cargaPorAnalista();

    /** Eventos activos sin asignacion activa (JPQL). */
    List<EventoResponse> eventosSinAsignar();
}
