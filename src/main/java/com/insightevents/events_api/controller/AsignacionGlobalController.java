package com.insightevents.events_api.controller;

import com.insightevents.events_api.dto.AsignacionResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import com.insightevents.events_api.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Vista global de asignaciones (todas, paginadas).
 */
@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionGlobalController {

    private final AsignacionService service;

    @GetMapping
    public PaginaResponse<AsignacionResponse> listar(
            @ParameterObject
            @PageableDefault(size = 10, sort = "fechaAsignacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarTodas(pageable);
    }
}
