package com.insightevents.events_api.service;

import com.insightevents.events_api.dto.CategoriaRequest;
import com.insightevents.events_api.dto.CategoriaResponse;
import java.util.List;

/**
 * Contrato del servicio de categorias. Definir la interfaz aparte de la
 * implementacion facilita el testeo (mocks) y desacopla al controller.
 */
public interface CategoriaService {

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    CategoriaResponse obtenerPorId(Long id);

    List<CategoriaResponse> listar();

    void eliminar(Long id);
}
