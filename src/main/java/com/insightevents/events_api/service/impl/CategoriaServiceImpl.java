package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.domain.Categoria;
import com.insightevents.events_api.dto.CategoriaRequest;
import com.insightevents.events_api.dto.CategoriaResponse;
import com.insightevents.events_api.exception.RecursoDuplicadoException;
import com.insightevents.events_api.exception.RecursoNoEncontradoException;
import com.insightevents.events_api.mapper.CategoriaMapper;
import com.insightevents.events_api.repository.CategoriaRepository;
import com.insightevents.events_api.service.CategoriaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de categorias.
 * Orquesta repositorio + mapper y aplica la regla de negocio de nombre unico.
 */
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        // Regla de negocio: no permitir nombres duplicados
        if (repository.existsByNombre(request.nombre())) {
            throw new RecursoDuplicadoException(
                    "Ya existe una categoria con el nombre '%s'".formatted(request.nombre()));
        }
        Categoria guardada = repository.save(mapper.toEntity(request));
        return mapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarOFallar(id);
        // El nombre nuevo no debe chocar con OTRA categoria distinta
        if (repository.existsByNombreAndIdNot(request.nombre(), id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe otra categoria con el nombre '%s'".formatted(request.nombre()));
        }
        mapper.updateEntity(categoria, request);
        // No hace falta save(): entidad managed dentro de @Transactional (dirty checking)
        return mapper.toResponse(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarOFallar(id);
        repository.delete(categoria);
    }

    /** Helper: busca la categoria o lanza 404 si no existe. */
    private Categoria buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", id));
    }
}
