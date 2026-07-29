package com.insightevents.events_api.dto;

/**
 * Datos de una categoria que la API devuelve al cliente.
 */
public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion
) {}
