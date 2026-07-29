package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.dto.CargaAnalistaResponse;
import com.insightevents.events_api.dto.EventoResponse;
import com.insightevents.events_api.mapper.EventoMapper;
import com.insightevents.events_api.repository.AsignacionRepository;
import com.insightevents.events_api.repository.EventoRepository;
import com.insightevents.events_api.service.ReporteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de reportes.
 */
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final AsignacionRepository asignacionRepository;
    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CargaAnalistaResponse> cargaPorAnalista() {
        return asignacionRepository.cargaPorAnalista().stream()
                .map(p -> new CargaAnalistaResponse(
                        p.getAnalistaId(), p.getAnalistaNombre(), p.getTotalAsignaciones()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> eventosSinAsignar() {
        return eventoRepository.eventosSinAsignar().stream()
                .map(eventoMapper::toResponse)
                .toList();
    }
}
