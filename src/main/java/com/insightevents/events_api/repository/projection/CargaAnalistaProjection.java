package com.insightevents.events_api.repository.projection;

/**
 * Projection de interfaz para mapear el resultado del reporte de carga por
 * analista (consulta SQL nativa). Spring Data crea la implementacion a partir
 * de los alias de columnas de la consulta.
 */
public interface CargaAnalistaProjection {

    Long getAnalistaId();

    String getAnalistaNombre();

    long getTotalAsignaciones();
}
