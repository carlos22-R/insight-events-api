package com.insightevents.events_api.service;

import com.insightevents.events_api.dto.AnalistaRequest;
import com.insightevents.events_api.dto.AnalistaResponse;
import java.util.List;

/**
 * Contrato del servicio de analistas.
 */
public interface AnalistaService {

    AnalistaResponse crear(AnalistaRequest request);

    AnalistaResponse actualizar(Long id, AnalistaRequest request);

    AnalistaResponse obtenerPorId(Long id);

    List<AnalistaResponse> listar();

    void eliminar(Long id);
}
