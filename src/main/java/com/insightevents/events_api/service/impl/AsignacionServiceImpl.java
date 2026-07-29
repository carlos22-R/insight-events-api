package com.insightevents.events_api.service.impl;

import com.insightevents.events_api.domain.Asignacion;
import com.insightevents.events_api.domain.Evento;
import com.insightevents.events_api.domain.enums.EstadoAsignacion;
import com.insightevents.events_api.domain.enums.EstadoEvento;
import com.insightevents.events_api.domain.enums.TipoAccion;
import com.insightevents.events_api.dto.AsignacionResponse;
import com.insightevents.events_api.exception.RecursoDuplicadoException;
import com.insightevents.events_api.exception.RecursoNoEncontradoException;
import com.insightevents.events_api.mapper.AsignacionMapper;
import com.insightevents.events_api.repository.AsignacionRepository;
import com.insightevents.events_api.service.AsignacionService;
import com.insightevents.events_api.service.RegistroHistorial;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de asignaciones. Delega la logica en el stored
 * procedure y traduce sus errores (SQLSTATE propios) a excepciones de negocio.
 */
@Service
@RequiredArgsConstructor
public class AsignacionServiceImpl implements AsignacionService {

    // SQLSTATE definidos en el stored procedure asignar_evento
    private static final String EVENTO_NO_EXISTE   = "EV404";
    private static final String ANALISTA_NO_EXISTE = "AN404";
    private static final String DUPLICADO          = "DUP09";

    private final AsignacionRepository repository;
    private final AsignacionMapper mapper;
    private final RegistroHistorial registroHistorial;

    @Override
    @Transactional
    public AsignacionResponse asignar(Long eventoId, Long analistaId, String usuario, String comentario) {
        try {
            repository.asignarEvento(eventoId, analistaId, usuario, comentario);
        } catch (RuntimeException ex) {
            traducirError(ex, eventoId, analistaId);   // lanza la excepcion de negocio adecuada
        }
        // Recuperamos la asignacion recien creada
        Asignacion creada = repository.findByEventoIdAndAnalistaId(eventoId, analistaId)
                .orElseThrow(() -> new IllegalStateException("No se encontro la asignacion recien creada"));
        // Al asignar, el evento pasa a EN_INVESTIGACION (alguien lo esta investigando)
        creada.getEvento().setEstado(EstadoEvento.EN_INVESTIGACION);
        return mapper.toResponse(creada);
    }

    @Override
    @Transactional
    public AsignacionResponse resolver(Long eventoId, Long asignacionId, String usuario) {
        Asignacion asignacion = repository.findById(asignacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion", asignacionId));
        // La asignacion debe pertenecer al evento indicado en la URL
        if (!asignacion.getEvento().getId().equals(eventoId)) {
            throw new RecursoNoEncontradoException("Asignacion", asignacionId);
        }

        Evento evento = asignacion.getEvento();

        // Al resolver el evento, TODAS sus asignaciones activas se finalizan
        // (incluida la del analista). Asi no quedan asignaciones activas sobre
        // un evento ya resuelto.
        List<Asignacion> activas =
                repository.findByEventoIdAndEstado(eventoId, EstadoAsignacion.ACTIVA);
        activas.forEach(a -> a.setEstado(EstadoAsignacion.FINALIZADA));
        asignacion.setEstado(EstadoAsignacion.FINALIZADA);   // por si no estaba activa
        evento.setEstado(EstadoEvento.RESUELTO);

        long otras = activas.stream().filter(a -> !a.getId().equals(asignacionId)).count();
        String comentario = otras > 0
                ? "Evento resuelto por %s; %d asignacion(es) de otros analistas finalizada(s) automaticamente"
                        .formatted(usuario, otras)
                : "Evento resuelto por %s".formatted(usuario);
        // El historial guarda en 'usuario' QUIEN resolvio el evento
        registroHistorial.registrar(evento, usuario, TipoAccion.CAMBIO_ESTADO, comentario);

        return mapper.toResponse(asignacion);
    }

    /** Traduce el SQLSTATE del stored procedure a la excepcion HTTP correcta. */
    private void traducirError(RuntimeException ex, Long eventoId, Long analistaId) {
        String sqlState = extraerSqlState(ex);
        if (sqlState == null) {
            throw ex;   // no es un error conocido del SP: se propaga
        }
        switch (sqlState) {
            case EVENTO_NO_EXISTE   -> throw new RecursoNoEncontradoException("Evento", eventoId);
            case ANALISTA_NO_EXISTE -> throw new RecursoNoEncontradoException("Analista", analistaId);
            case DUPLICADO          -> throw new RecursoDuplicadoException(
                    "El evento %d ya esta asignado al analista %d".formatted(eventoId, analistaId));
            default                 -> throw ex;
        }
    }

    /** Recorre la cadena de causas buscando el SQLSTATE de la SQLException. */
    private String extraerSqlState(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual instanceof SQLException sqlEx) {
                return sqlEx.getSQLState();
            }
            actual = actual.getCause();
        }
        return null;
    }
}
