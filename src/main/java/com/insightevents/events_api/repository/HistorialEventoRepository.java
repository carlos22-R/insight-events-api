package com.insightevents.events_api.repository;

import com.insightevents.events_api.domain.HistorialEvento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio del historial de eventos.
 */
public interface HistorialEventoRepository extends JpaRepository<HistorialEvento, Long> {

    /**
     * JPQL: historial completo de un evento, del mas reciente al mas antiguo.
     */
    @Query("""
            SELECT h FROM HistorialEvento h
            WHERE h.evento.id = :eventoId
            ORDER BY h.fecha DESC, h.id DESC
            """)
    List<HistorialEvento> historialDeEvento(@Param("eventoId") Long eventoId);
}
