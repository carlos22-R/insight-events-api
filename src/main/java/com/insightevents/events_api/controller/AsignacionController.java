package com.insightevents.events_api.controller;

import com.insightevents.events_api.dto.AsignacionResponse;
import com.insightevents.events_api.dto.AsignarEventoRequest;
import com.insightevents.events_api.service.AsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint para asignar un evento a un analista (usa el stored procedure).
 */
@RestController
@RequestMapping("/api/eventos/{eventoId}/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService service;

    /** Asigna el evento a un analista. El usuario responsable va en X-Usuario. */
    @PostMapping
    public ResponseEntity<AsignacionResponse> asignar(
            @PathVariable Long eventoId,
            @Valid @RequestBody AsignarEventoRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        AsignacionResponse creada =
                service.asignar(eventoId, request.analistaId(), usuario, request.comentario());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /** Resuelve una asignacion: la asignacion pasa a FINALIZADA y el evento a CERRADO. */
    @PostMapping("/{asignacionId}/resolver")
    public AsignacionResponse resolver(
            @PathVariable Long eventoId,
            @PathVariable Long asignacionId,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return service.resolver(eventoId, asignacionId, usuario);
    }
}
