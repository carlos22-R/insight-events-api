package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.domain.Analista;
import com.insightevents.events_api.dto.AnalistaRequest;
import com.insightevents.events_api.dto.AnalistaResponse;
import com.insightevents.events_api.exception.RecursoDuplicadoException;
import com.insightevents.events_api.exception.RecursoNoEncontradoException;
import com.insightevents.events_api.mapper.AnalistaMapper;
import com.insightevents.events_api.repository.AnalistaRepository;
import com.insightevents.events_api.service.AnalistaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final AnalistaMapper mapper;

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
        repository.delete(analista);
    }

    private Analista buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Analista", id));
    }
}
