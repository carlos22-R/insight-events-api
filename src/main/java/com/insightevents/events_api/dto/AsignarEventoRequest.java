package com.insightevents.events_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos para asignar un evento a un analista. El evento viene en la URL y el
 * usuario responsable en el header X-Usuario.
 */
public record AsignarEventoRequest(

        @NotNull(message = "El analista es obligatorio")
        Long analistaId,

        @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
        String comentario
) {}
