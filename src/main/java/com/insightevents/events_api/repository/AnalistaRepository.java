package com.insightevents.events_api.repository;

import com.insightevents.events_api.domain.Analista;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Analista. Metodos derivados para validar unicidad del correo.
 */
public interface AnalistaRepository extends JpaRepository<Analista, Long> {

    /** ¿Existe ya un analista con ese correo? (al crear). */
    boolean existsByCorreo(String correo);

    /** ¿Existe OTRO analista (distinto de id) con ese correo? (al actualizar). */
    boolean existsByCorreoAndIdNot(String correo, Long id);
}
