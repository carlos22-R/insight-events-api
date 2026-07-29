package com.insightevents.events_api.repository;

import com.insightevents.events_api.domain.Asignacion;
import com.insightevents.events_api.domain.enums.EstadoAsignacion;
import com.insightevents.events_api.repository.projection.CargaAnalistaProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de Asignacion. Incluye la llamada al stored procedure de
 * asignacion (@Procedure) y metodos derivados de apoyo.
 */
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    /**
     * Invoca el stored procedure asignar_evento. Toda la logica (validar
     * evento, validar analista, evitar duplicados y registrar historial)
     * ocurre dentro de la base de datos, de forma atomica.
     */
    @Procedure(procedureName = "asignar_evento")
    void asignarEvento(Long p_evento_id, Long p_analista_id, String p_usuario, String p_comentario);

    /** Para la integridad RESTRICT: ¿el analista tiene asignaciones? */
    boolean existsByAnalistaId(Long analistaId);

    /** Recupera la asignacion recien creada para devolverla en la respuesta. */
    Optional<Asignacion> findByEventoIdAndAnalistaId(Long eventoId, Long analistaId);

    /** Asignaciones de un evento en un estado dado (p.ej. las ACTIVA al cancelar). */
    List<Asignacion> findByEventoIdAndEstado(Long eventoId, EstadoAsignacion estado);

    /** Todas las asignaciones de un evento (para listarlas con su id). */
    List<Asignacion> findByEventoId(Long eventoId);

    /**
     * JPQL: asignaciones activas de un analista sobre eventos activos, paginadas.
     * Se consulta sobre Asignacion para poder exponer el id de la asignacion.
     */
    @Query("""
            SELECT a FROM Asignacion a
            WHERE a.analista.id = :analistaId
              AND a.estado = com.insightevents.events_api.domain.enums.EstadoAsignacion.ACTIVA
              AND a.evento.activo = true
            """)
    Page<Asignacion> asignacionesActivasDeAnalista(@Param("analistaId") Long analistaId, Pageable pageable);

    /**
     * SQL nativo: carga de trabajo por analista (asignaciones activas).
     * LEFT JOIN para incluir tambien a los analistas con 0 asignaciones.
     * Los alias van entre comillas para conservar el camelCase que espera
     * la projection de interfaz.
     */
    @Query(value = """
            SELECT an.id       AS "analistaId",
                   an.nombre   AS "analistaNombre",
                   COUNT(a.id) AS "totalAsignaciones"
            FROM analista an
            LEFT JOIN asignacion a
                   ON a.analista_id = an.id AND a.estado = 'ACTIVA'
            GROUP BY an.id, an.nombre
            ORDER BY COUNT(a.id) DESC, an.nombre ASC
            """, nativeQuery = true)
    List<CargaAnalistaProjection> cargaPorAnalista();
}
