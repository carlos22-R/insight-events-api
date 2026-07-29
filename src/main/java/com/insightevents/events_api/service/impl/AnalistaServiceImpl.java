package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.domain.Analista;
import com.insightevents.events_api.dto.AnalistaRequest;
import com.insightevents.events_api.dto.AnalistaResponse;
import com.insightevents.events_api.dto.EventoAsignadoResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import com.insightevents.events_api.exception.RecursoDuplicadoException;
import com.insightevents.events_api.exception.RecursoEnUsoException;
import com.insightevents.events_api.exception.RecursoNoEncontradoException;
import com.insightevents.events_api.mapper.AnalistaMapper;
import com.insightevents.events_api.mapper.AsignacionMapper;
import com.insightevents.events_api.repository.AnalistaRepository;
import com.insightevents.events_api.repository.AsignacionRepository;
import com.insightevents.events_api.service.AnalistaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de analistas.
 * Aplica la regla de negocio de correo unico.
 */
@Service
@RequiredArgsConstructor
public class AnalistaServiceImpl implements AnalistaService {

    private final AnalistaRepository repository;
    private final AsignacionRepository asignacionRepository;
    private final AnalistaMapper mapper;
    private final AsignacionMapper asignacionMapper;

    @Override
    @Transactional
    public AnalistaResponse crear(AnalistaRequest request) {
        if (repository.existsByCorreo(request.correo())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un analista con el correo '%s'".formatted(request.correo()));
        }
        Analista guardado = repository.save(mapper.toEntity(request));
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public AnalistaResponse actualizar(Long id, AnalistaRequest request) {
        Analista analista = buscarOFallar(id);
        if (repository.existsByCorreoAndIdNot(request.correo(), id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe otro analista con el correo '%s'".formatted(request.correo()));
        }
        mapper.updateEntity(analista, request);
        return mapper.toResponse(analista);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalistaResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalistaResponse> listar() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Analista analista = buscarOFallar(id);
        // Integridad (RESTRICT): no borrar un analista con asignaciones
        if (asignacionRepository.existsByAnalistaId(id)) {
            throw new RecursoEnUsoException(
                    "No se puede eliminar el analista: tiene asignaciones asociadas");
        }
        repository.delete(analista);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<EventoAsignadoResponse> eventosAsignados(Long analistaId, Pageable pageable) {
        // Validamos que el analista exista para devolver 404 si no
        if (!repository.existsById(analistaId)) {
            throw new RecursoNoEncontradoException("Analista", analistaId);
        }
        Page<EventoAsignadoResponse> pagina = asignacionRepository
                .asignacionesActivasDeAnalista(analistaId, pageable)
                .map(asignacionMapper::toEventoAsignado);
        return PaginaResponse.de(pagina);
    }

    private Analista buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Analista", id));
    }
}
