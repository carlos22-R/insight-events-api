package com.insightevents.events_api.repository;

import com.insightevents.events_api.domain.Evento;
import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de Evento. Combina varios mecanismos de acceso a datos:
 * metodos derivados, JPQL con filtros dinamicos + paginacion, y SQL nativo.
 */
public interface EventoRepository extends JpaRepository<Evento, Long> {

    /** Metodo derivado: busca un evento activo por id (respeta el soft delete). */
    Optional<Evento> findByIdAndActivoTrue(Long id);

    /** Metodo derivado: ¿hay eventos que usen esta categoria? (para RESTRICT al borrar). */
    boolean existsByCategoriaId(Long categoriaId);

    /**
     * JPQL con filtros dinamicos + paginacion.
     * Cada filtro se aplica solo si su parametro viene con valor (patron
     * ":param IS NULL OR ..."). Devuelve una Page (contenido + metadatos).
     */
    @Query("""
            SELECT e FROM Evento e
            WHERE e.activo = true
              AND (:categoriaId IS NULL OR e.categoria.id = :categoriaId)
              AND (:estado      IS NULL OR e.estado = :estado)
              AND (:prioridad   IS NULL OR e.prioridad = :prioridad)
              AND (CAST(:fechaDesde AS timestamp) IS NULL OR e.fecha >= :fechaDesde)
              AND (CAST(:fechaHasta AS timestamp) IS NULL OR e.fecha <= :fechaHasta)
              AND (:texto       IS NULL
                   OR LOWER(e.titulo)      LIKE LOWER(CONCAT('%', CAST(:texto AS string), '%'))
                   OR LOWER(e.descripcion) LIKE LOWER(CONCAT('%', CAST(:texto AS string), '%')))
            """)
    Page<Evento> buscar(@Param("categoriaId") Long categoriaId,
                        @Param("estado") EstadoEvento estado,
                        @Param("prioridad") Prioridad prioridad,
                        @Param("fechaDesde") LocalDateTime fechaDesde,
                        @Param("fechaHasta") LocalDateTime fechaHasta,
                        @Param("texto") String texto,
                        Pageable pageable);

    /** SQL nativo: obtiene el siguiente valor de la secuencia del codigo. */
    @Query(value = "SELECT nextval('evento_codigo_seq')", nativeQuery = true)
    long siguienteValorCodigo();

    /**
     * JPQL: eventos abiertos (NUEVO o EN_INVESTIGACION) que no tienen ninguna
     * asignacion activa, es decir, pendientes de que alguien los tome. Excluye
     * los ya RESUELTO o DESCARTADO, que no requieren asignacion.
     */
    @Query("""
            SELECT e FROM Evento e
            WHERE e.activo = true
              AND e.estado IN (
                  com.insightevents.events_api.domain.enums.EstadoEvento.NUEVO,
                  com.insightevents.events_api.domain.enums.EstadoEvento.EN_INVESTIGACION
              )
              AND NOT EXISTS (
                  SELECT 1 FROM Asignacion a
                  WHERE a.evento = e
                    AND a.estado = com.insightevents.events_api.domain.enums.EstadoAsignacion.ACTIVA
              )
            ORDER BY e.fecha ASC
            """)
    List<Evento> eventosSinAsignar();
}
