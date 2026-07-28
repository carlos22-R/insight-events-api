# Plataforma Insight Events — API REST

> 🚧 **README en construcción.** Este es un placeholder; se completará con la
> documentación final (instrucciones de ejecución, decisiones de arquitectura y
> justificación de los mecanismos de acceso a datos).

API REST empresarial para la gestión e investigación de eventos (accidentes de
tránsito, reportes de clima, entre otros): permite administrarlos, clasificarlos,
asignarlos a analistas y mantener un historial completo de todas las acciones.

## Stack tecnológico

- **Java 21**
- **Spring Boot 4** (Spring Web, Spring Data JPA, Validation, Actuator)
- **Hibernate** (JPA)
- **PostgreSQL 16**
- **Flyway** (migraciones de base de datos)
- **Maven** (con Maven Wrapper)
- **Docker Compose** (PostgreSQL en contenedor)

## Arquitectura por capas

```
controller  ->  service  ->  repository  ->  PostgreSQL
   (dto)         (dto)        (entidades)
        mapper / exception / config (transversales)
```

- **controller** — endpoints REST, validación de entrada.
- **service** — lógica de negocio y transacciones.
- **repository** — acceso a datos (Spring Data JPA, JPQL, SQL nativo, stored procedure).
- **domain** — entidades JPA y enums.
- **dto** — contratos de la API (request/response).
- **mapper** — conversión entidad ↔ DTO.
- **exception** — manejo global de errores.
- **config** — configuración transversal.

## Estado del proyecto

| Fase | Estado |
|------|--------|
| Estructura y arquitectura base | ✅ |
| Configuración BD (Docker + Flyway) | ⏳ |
| Modelo de datos (5 entidades) | ⏳ |
| CRUD Categorías / Analistas | ⏳ |
| CRUD + búsqueda de Eventos | ⏳ |
| JPQL · SQL nativo · Stored procedure | ⏳ |
| Historial | ⏳ |
| Documentación final + Swagger | ⏳ |

---

_Prueba técnica — desarrollo en curso._
