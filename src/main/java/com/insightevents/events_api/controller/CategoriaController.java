package com.insightevents.events_api.controller;

import com.insightevents.events_api.dto.CategoriaRequest;
import com.insightevents.events_api.dto.CategoriaResponse;
import com.insightevents.events_api.service.CategoriaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
 * Endpoints REST para el CRUD de categorias.
 * El controller solo valida la entrada y delega en el servicio; no contiene
 * logica de negocio.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    /** Crear -> 201 Created con la ubicacion del nuevo recurso. */
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse creada = service.crear(request);
        URI location = URI.create("/api/categorias/" + creada.id());
        return ResponseEntity.created(location).body(creada);
    }

    /** Consultar una -> 200 OK (o 404 si no existe). */
    @GetMapping("/{id}")
    public CategoriaResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    /** Listar todas -> 200 OK. */
    @GetMapping
    public List<CategoriaResponse> listar() {
        return service.listar();
    }

    /** Editar -> 200 OK con la categoria actualizada. */
    @PutMapping("/{id}")
    public CategoriaResponse actualizar(@PathVariable Long id,
                                        @Valid @RequestBody CategoriaRequest request) {
        return service.actualizar(id, request);
    }

    /** Eliminar -> 204 No Content. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
