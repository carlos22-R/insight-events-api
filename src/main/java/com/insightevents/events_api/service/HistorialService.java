package com.insightevents.events_api.service;

import com.insightevents.events_api.dto.HistorialResponse;
import java.util.List;

/**
 * Contrato del servicio de historial.
 */
public interface HistorialService {

    /** Historial completo de acciones de un evento (mas reciente primero). */
    List<HistorialResponse> historialDeEvento(Long eventoId);
}
