package com.insightevents.events_api.mapper;

import com.insightevents.events_api.domain.Categoria;
import com.insightevents.events_api.domain.Evento;
import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.dto.EventoActualizarRequest;
import com.insightevents.events_api.dto.EventoCrearRequest;
import com.insightevents.events_api.dto.EventoResponse;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad Evento y sus DTOs.
 */
@Component
public class EventoMapper {

    /**
     * Crea la entidad a partir del request. Recibe la Categoria ya resuelta
     * por el service. El codigo NO se asigna aqui: lo pone el service porque
     * necesita la secuencia de base de datos.
     */
    public Evento toEntity(EventoCrearRequest dto, Categoria categoria) {
        Evento e = new Evento();
        e.setTitulo(dto.titulo());
        e.setDescripcion(dto.descripcion());
        e.setPrioridad(dto.prioridad());
        e.setFuente(dto.fuente());
        e.setCategoria(categoria);
        e.setFecha(dto.fecha() != null ? dto.fecha() : LocalDateTime.now());
        e.setEstado(EstadoEvento.NUEVO);   // estado inicial
        e.setActivo(true);
        return e;
    }

    /**
     * Actualizacion parcial: solo modifica los campos que vengan con valor.
     * La categoria se cambia solo si el service resolvio una nueva.
     */
    public void updateEntity(Evento e, EventoActualizarRequest dto, Categoria nuevaCategoria) {
        if (dto.titulo() != null)      e.setTitulo(dto.titulo());
        if (dto.descripcion() != null) e.setDescripcion(dto.descripcion());
        if (dto.prioridad() != null)   e.setPrioridad(dto.prioridad());
        if (dto.estado() != null)      e.setEstado(dto.estado());
        if (dto.fuente() != null)      e.setFuente(dto.fuente());
        if (nuevaCategoria != null)    e.setCategoria(nuevaCategoria);
    }

    /** Entidad -> DTO de salida (aplana la categoria a id + nombre). */
    public EventoResponse toResponse(Evento e) {
        Categoria c = e.getCategoria();
        return new EventoResponse(
                e.getId(),
                e.getCodigo(),
                e.getTitulo(),
                e.getDescripcion(),
                e.getFecha(),
                e.getPrioridad(),
                e.getEstado(),
                e.getFuente(),
                c != null ? c.getId() : null,
                c != null ? c.getNombre() : null,
                e.isActivo());
    }
}
