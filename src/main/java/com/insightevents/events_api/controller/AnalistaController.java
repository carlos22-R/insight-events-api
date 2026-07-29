package com.insightevents.events_api.controller;

import com.insightevents.events_api.dto.AnalistaRequest;
import com.insightevents.events_api.dto.AnalistaResponse;
import com.insightevents.events_api.dto.EventoAsignadoResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import com.insightevents.events_api.service.AnalistaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST para el CRUD de analistas.
 */
@RestController
@RequestMapping("/api/analistas")
@RequiredArgsConstructor
public class AnalistaController {

    private final AnalistaService service;

    @PostMapping
    public ResponseEntity<AnalistaResponse> crear(@Valid @RequestBody AnalistaRequest request) {
        AnalistaResponse creado = service.crear(request);
        URI location = URI.create("/api/analistas/" + creado.id());
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping("/{id}")
    public AnalistaResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping
    public List<AnalistaResponse> listar() {
        return service.listar();
    }

    @PutMapping("/{id}")
    public AnalistaResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody AnalistaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    /** Eventos activos asignados a un analista (con id de asignacion, paginado). */
    @GetMapping("/{id}/eventos")
    public PaginaResponse<EventoAsignadoResponse> eventosAsignados(
            @PathVariable Long id,
            @ParameterObject
            @PageableDefault(size = 10, sort = "fechaAsignacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.eventosAsignados(id, pageable);
    }
}
