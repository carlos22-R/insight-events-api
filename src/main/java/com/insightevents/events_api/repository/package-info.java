/**
 * Capa de acceso a datos: interfaces de Spring Data JPA.
 *
 * <p>Aqui conviven los distintos mecanismos que pide la prueba:
 * metodos derivados ({@code findBy...}) de forma selectiva, consultas
 * JPQL ({@code @Query}), SQL nativo ({@code @Query(nativeQuery = true)})
 * y la invocacion del stored procedure de asignacion.
 */
package com.insightevents.events_api.repository;
