package com.insightevents.events_api.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Envoltura de paginacion propia para la API. Evita exponer la clase Page de
 * Spring (cuya forma JSON no es estable) y da un contrato claro al cliente.
 *
 * @param contenido      elementos de la pagina actual
 * @param pagina         indice de pagina (empieza en 0)
 * @param tamano         tamano de pagina solicitado
 * @param totalElementos total de elementos que cumplen el filtro
 * @param totalPaginas   total de paginas disponibles
 * @param ultima         true si es la ultima pagina
 */
public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean ultima
) {
    public static <T> PaginaResponse<T> de(Page<T> page) {
        return new PaginaResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
