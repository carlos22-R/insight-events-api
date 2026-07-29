package com.insightevents.events_api.controller;

import com.insightevents.events_api.dto.HistorialResponse;
import com.insightevents.events_api.service.HistorialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint para consultar el historial completo de un evento.
 */
@RestController
@RequestMapping("/api/eventos/{eventoId}/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService service;

    @GetMapping
    public List<HistorialResponse> historial(@PathVariable Long eventoId) {
        return service.historialDeEvento(eventoId);
    }
}
