/**
 * Objetos de transferencia de datos (DTOs): request y response.
 *
 * <p>Desacoplan la API de las entidades JPA. Evitan exponer el modelo
 * interno, previenen problemas de serializacion (lazy loading) y
 * permiten versionar el contrato de la API con independencia del schema.
 */
package com.insightevents.events_api.dto;
