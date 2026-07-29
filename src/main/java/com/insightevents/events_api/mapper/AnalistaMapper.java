package com.insightevents.events_api.mapper;

import com.insightevents.events_api.domain.Analista;
import com.insightevents.events_api.dto.AnalistaRequest;
import com.insightevents.events_api.dto.AnalistaResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad Analista y sus DTOs.
 */
@Component
public class AnalistaMapper {

    public Analista toEntity(AnalistaRequest dto) {
        Analista a = new Analista();
        a.setNombre(dto.nombre());
        a.setCorreo(dto.correo());
        return a;
    }

    public void updateEntity(Analista a, AnalistaRequest dto) {
        a.setNombre(dto.nombre());
        a.setCorreo(dto.correo());
    }

    public AnalistaResponse toResponse(Analista a) {
        return new AnalistaResponse(a.getId(), a.getNombre(), a.getCorreo());
    }

    public List<AnalistaResponse> toResponseList(List<Analista> analistas) {
        return analistas.stream().map(this::toResponse).toList();
    }
}
