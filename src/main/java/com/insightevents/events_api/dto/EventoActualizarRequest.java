package com.insightevents.events_api.dto;

import com.insightevents.events_api.domain.enums.Prioridad;
import jakarta.validation.constraints.Size;

/**
 * Datos para actualizar un evento. Todos los campos son opcionales:
 * solo se modifican los que vengan con valor (actualizacion parcial).
 *
 * Nota: el estado NO se actualiza aqui a proposito. Las transiciones de estado
 * las controlan acciones dedicadas (asignar, resolver, cancelar).
 */
public record EventoActualizarRequest(

        @Size(max = 200, message = "El titulo no puede superar 200 caracteres")
        String titulo,

        @Size(max = 5000, message = "La descripcion no puede superar 5000 caracteres")
        String descripcion,

        Prioridad prioridad,

        @Size(max = 150, message = "La fuente no puede superar 150 caracteres")
        String fuente,

        Long categoriaId
) {}
