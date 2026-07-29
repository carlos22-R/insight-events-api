package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.dto.HistorialResponse;
import com.insightevents.events_api.exception.RecursoNoEncontradoException;
import com.insightevents.events_api.mapper.HistorialMapper;
import com.insightevents.events_api.repository.EventoRepository;
import com.insightevents.events_api.repository.HistorialEventoRepository;
import com.insightevents.events_api.service.HistorialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de historial.
 */
@Service
@RequiredArgsConstructor
public class HistorialServiceImpl implements HistorialService {

    private final HistorialEventoRepository repository;
    private final EventoRepository eventoRepository;
    private final HistorialMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<HistorialResponse> historialDeEvento(Long eventoId) {
        // Un evento borrado logicamente igual conserva su historial (auditoria),
        // por eso usamos existsById (no filtramos por activo).
        if (!eventoRepository.existsById(eventoId)) {
            throw new RecursoNoEncontradoException("Evento", eventoId);
        }
        return mapper.toResponseList(repository.historialDeEvento(eventoId));
    }
}
