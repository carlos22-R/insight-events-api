package com.insightevents.events_api.service;

import com.insightevents.events_api.domain.Evento;
import com.insightevents.events_api.domain.HistorialEvento;
import com.insightevents.events_api.domain.enums.TipoAccion;
import com.insightevents.events_api.repository.HistorialEventoRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Componente reutilizable para registrar acciones en el historial de un evento.
 * Lo usan varios servicios (eventos, asignaciones) para no duplicar la logica.
 */
@Component
@RequiredArgsConstructor
public class RegistroHistorial {

    private final HistorialEventoRepository repository;

    public void registrar(Evento evento, String usuario, TipoAccion accion, String comentario) {
        HistorialEvento h = new HistorialEvento();
        h.setEvento(evento);
        h.setUsuario(usuario != null && !usuario.isBlank() ? usuario : "sistema");
        h.setFecha(LocalDateTime.now());
        h.setAccion(accion);
        h.setComentario(comentario);
        repository.save(h);
    }
}
