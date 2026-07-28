/**
 * Manejo de errores: excepciones de negocio y manejador global.
 *
 * <p>Un {@code @RestControllerAdvice} centraliza la traduccion de
 * excepciones a respuestas HTTP consistentes (404, 409, 400, ...),
 * con un cuerpo de error uniforme para toda la API.
 */
package com.insightevents.events_api.exception;
