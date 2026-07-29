package com.insightevents.events_api.dto;

import com.insightevents.events_api.domain.enums.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Datos para crear un evento. El codigo, el estado inicial (NUEVO) y la fecha
 * (si no se envia) los asigna el sistema, no el cliente.
 */
public record EventoCrearRequest(

        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 200, message = "El titulo no puede superar 200 caracteres")
        String titulo,

        @Size(max = 5000, message = "La descripcion no puede superar 5000 caracteres")
        String descripcion,

        @NotNull(message = "La prioridad es obligatoria")
        Prioridad prioridad,

        @Size(max = 150, message = "La fuente no puede superar 150 caracteres")
        String fuente,

        /** Opcional: si no se envia, se usa la fecha/hora actual. */
        LocalDateTime fecha,

        @NotNull(message = "La categoria es obligatoria")
        Long categoriaId
) {}
