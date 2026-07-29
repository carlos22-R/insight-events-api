package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.domain.Categoria;
import com.insightevents.events_api.domain.Evento;
import com.insightevents.events_api.domain.HistorialEvento;
import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.Prioridad;
import com.insightevents.events_api.domain.enums.TipoAccion;
import com.insightevents.events_api.dto.EventoActualizarRequest;
import com.insightevents.events_api.dto.EventoCrearRequest;
import com.insightevents.events_api.dto.EventoResponse;
import com.insightevents.events_api.dto.PaginaResponse;
import com.insightevents.events_api.exception.RecursoNoEncontradoException;
import com.insightevents.events_api.mapper.EventoMapper;
import com.insightevents.events_api.repository.CategoriaRepository;
import com.insightevents.events_api.repository.EventoRepository;
import com.insightevents.events_api.repository.HistorialEventoRepository;
import com.insightevents.events_api.service.EventoService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de eventos.
 */
@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final HistorialEventoRepository historialRepository;
    private final EventoMapper mapper;

    @Override
    @Transactional
    public EventoResponse crear(EventoCrearRequest request, String usuario) {
        Categoria categoria = resolverCategoria(request.categoriaId());
        Evento evento = mapper.toEntity(request, categoria);
        evento.setCodigo(generarCodigo());
        Evento guardado = repository.save(evento);
        registrarHistorial(guardado, usuario, TipoAccion.CREACION,
                "Evento creado con codigo " + guardado.getCodigo());
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public EventoResponse actualizar(Long id, EventoActualizarRequest request, String usuario) {
        Evento evento = buscarActivoOFallar(id);
        EstadoEvento estadoAnterior = evento.getEstado();   // para detectar cambio de estado

        // Solo resolvemos categoria nueva si el cliente la envio
        Categoria nuevaCategoria =
                request.categoriaId() != null ? resolverCategoria(request.categoriaId()) : null;
        mapper.updateEntity(evento, request, nuevaCategoria);
        // Dirty checking: no hace falta save()

        // Si cambio el estado, lo registramos como CAMBIO_ESTADO; si no, ACTUALIZACION
        if (request.estado() != null && request.estado() != estadoAnterior) {
            registrarHistorial(evento, usuario, TipoAccion.CAMBIO_ESTADO,
                    "Estado: %s -> %s".formatted(estadoAnterior, evento.getEstado()));
        } else {
            registrarHistorial(evento, usuario, TipoAccion.ACTUALIZACION, "Evento actualizado");
        }
        return mapper.toResponse(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public EventoResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarActivoOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<EventoResponse> buscar(Long categoriaId, EstadoEvento estado,
                                                 Prioridad prioridad, LocalDateTime fechaDesde,
                                                 LocalDateTime fechaHasta, String texto,
                                                 Pageable pageable) {
        // Normaliza texto vacio a null para que el filtro se ignore
        String textoFiltro = (texto != null && !texto.isBlank()) ? texto.trim() : null;
        Page<EventoResponse> pagina = repository
                .buscar(categoriaId, estado, prioridad, fechaDesde, fechaHasta, textoFiltro, pageable)
                .map(mapper::toResponse);
        return PaginaResponse.de(pagina);
    }

    @Override
    @Transactional
    public void eliminar(Long id, String usuario) {
        Evento evento = buscarActivoOFallar(id);
        evento.setActivo(false);   // borrado logico (soft delete)
        registrarHistorial(evento, usuario, TipoAccion.ELIMINACION,
                "Evento marcado como eliminado ");
    }

    /* ===================== helpers ===================== */

    /** Registra una accion en el historial del evento. */
    private void registrarHistorial(Evento evento, String usuario, TipoAccion accion, String comentario) {
        HistorialEvento h = new HistorialEvento();
        h.setEvento(evento);
        h.setUsuario(usuario != null && !usuario.isBlank() ? usuario : "sistema");
        h.setFecha(LocalDateTime.now());
        h.setAccion(accion);
        h.setComentario(comentario);
        historialRepository.save(h);
    }

    /** Genera el codigo legible EVT-000001 usando la secuencia de la BD. */
    private String generarCodigo() {
        long n = repository.siguienteValorCodigo();
        return "EVT-%06d".formatted(n);
    }

    private Categoria resolverCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", categoriaId));
    }

    private Evento buscarActivoOFallar(Long id) {
        return repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento", id));
    }
}
