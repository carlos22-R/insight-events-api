package com.insightevents.events_api.controller;

import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import com.insightevents.events_api.dto.EventoActualizarRequest;
import com.insightevents.events_api.dto.EventoCrearRequest;
import com.insightevents.events_api.dto.EventoResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import com.insightevents.events_api.service.EventoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST para eventos: CRUD + busqueda con filtros y paginacion.
 */
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;

    @PostMapping
    public ResponseEntity<EventoResponse> crear(
            @Valid @RequestBody EventoCrearRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        EventoResponse creado = service.crear(request, usuario);
        return ResponseEntity.created(URI.create("/api/eventos/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public EventoResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    /**
     * Busqueda con filtros opcionales + paginacion.
     * Ej: GET /api/eventos?estado=NUEVO&prioridad=ALTA&texto=choque&page=0&size=10&sort=fecha,desc
     * Spring inyecta el Pageable automaticamente a partir de page/size/sort.
     */
    @GetMapping
    public PaginaResponse<EventoResponse> buscar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) EstadoEvento estado,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) String texto,
            @ParameterObject
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.buscar(categoriaId, estado, prioridad, fechaDesde, fechaHasta, texto, pageable);
    }

    @PutMapping("/{id}")
    public EventoResponse actualizar(@PathVariable Long id,
                                     @Valid @RequestBody EventoActualizarRequest request,
                                     @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return service.actualizar(id, request, usuario);
    }

    /** Borrado logico (soft delete) -> 204. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        service.eliminar(id, usuario);
    }

    /** Cancela el evento (DESCARTADO) y cancela sus asignaciones activas. */
    @PostMapping("/{id}/cancelar")
    public EventoResponse cancelar(@PathVariable Long id,
                                   @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return service.cancelar(id, usuario);
    }
}
