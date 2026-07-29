package com.insightevents.events_api.mapper;

import com.insightevents.events_api.domain.Categoria;
import com.insightevents.events_api.dto.CategoriaRequest;
import com.insightevents.events_api.dto.CategoriaResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad Categoria y sus DTOs.
 * Concentra aqui el mapeo para no repetirlo en servicios ni controllers.
 */
@Component
public class CategoriaMapper {

    /** DTO de entrada -> entidad nueva (para crear). */
    public Categoria toEntity(CategoriaRequest dto) {
        Categoria c = new Categoria();
        c.setNombre(dto.nombre());
        c.setDescripcion(dto.descripcion());
        return c;
    }

    /** Vuelca cambios sobre una entidad existente (para actualizar). */
    public void updateEntity(Categoria c, CategoriaRequest dto) {
        c.setNombre(dto.nombre());
        c.setDescripcion(dto.descripcion());
    }

    /** Entidad -> DTO de salida (para responder). */
    public CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }

    /** Lista de entidades -> lista de DTOs (reutiliza toResponse). */
    public List<CategoriaResponse> toResponseList(List<Categoria> categorias) {
        return categorias.stream().map(this::toResponse).toList();
    }
}
