package com.insightevents.events_api.repository;

import com.insightevents.events_api.domain.Asignacion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

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
}
