package com.insightevents.events_api.dto;

/**
 * Datos de un analista que la API devuelve al cliente.
 */
public record AnalistaResponse(
        Long id,
        String nombre,
        String correo
) {}
