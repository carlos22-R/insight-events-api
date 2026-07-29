package com.insightevents.events_api.repository;

import com.insightevents.events_api.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Categoria.
 *
 * <p>Al extender {@link JpaRepository} ya obtenemos gratis save, findById,
 * findAll, deleteById, etc. Solo agregamos metodos derivados puntuales
 * para validar unicidad del nombre (uso apropiado de {@code findBy/existsBy}).
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /** ¿Existe ya una categoria con ese nombre? (para validar al crear). */
    boolean existsByNombre(String nombre);

    /**
     * ¿Existe otra categoria (distinta de {@code id}) con ese nombre?
     * Se usa al actualizar, para no chocar con la propia fila que editamos.
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
