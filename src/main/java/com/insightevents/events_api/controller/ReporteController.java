package com.insightevents.events_api.controller;

import com.insightevents.events_api.dto.CargaAnalistaResponse;
import com.insightevents.events_api.dto.EventoResponse;
import com.insightevents.events_api.service.ReporteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de reportes/estadisticas.
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    /** Carga de trabajo por analista (asignaciones activas). */
    @GetMapping("/carga-analistas")
    public List<CargaAnalistaResponse> cargaPorAnalista() {
        return service.cargaPorAnalista();
    }

    /** Eventos activos que nadie esta investigando. */
    @GetMapping("/eventos-sin-asignar")
    public List<EventoResponse> eventosSinAsignar() {
        return service.eventosSinAsignar();
    }
}
