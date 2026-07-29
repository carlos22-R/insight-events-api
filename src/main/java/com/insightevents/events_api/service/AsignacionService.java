package com.insightevents.events_api.service;

import com.insightevents.events_api.dto.AsignacionResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Contrato del servicio de asignaciones.
 */
public interface AsignacionService {

    /**
     * Asigna un evento a un analista invocando el stored procedure.
     * @param usuario responsable de la accion (para el historial)
     */
    AsignacionResponse asignar(Long eventoId, Long analistaId, String usuario, String comentario);

    /**
     * Resuelve una asignacion: la asignacion pasa a FINALIZADA y su evento a RESUELTO.
     */
    AsignacionResponse resolver(Long eventoId, Long asignacionId, String usuario);

    /** Lista las asignaciones de un evento (con su id). */
    List<AsignacionResponse> listarPorEvento(Long eventoId);

    /** Lista todas las asignaciones, paginadas. */
    PaginaResponse<AsignacionResponse> listarTodas(Pageable pageable);
}
