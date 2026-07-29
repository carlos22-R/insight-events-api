# Plataforma Insight Events — API REST

API REST empresarial para la **gestión e investigación de eventos** (accidentes de
tránsito, reportes de clima, incidentes de seguridad, etc.). Permite administrar
eventos, clasificarlos por categoría, asignarlos a analistas para su investigación
y mantener un **historial completo y automático** de todas las acciones realizadas.

> Repositorio: https://github.com/carlos22-R/insight-events-api

---

## Índice

- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
- [Documentación de la API (Swagger)](#documentación-de-la-api-swagger)
- [Arquitectura y decisiones de diseño](#arquitectura-y-decisiones-de-diseño)
- [Modelo de datos](#modelo-de-datos)
- [Ciclo de vida de un evento](#ciclo-de-vida-de-un-evento)
- [Endpoints principales](#endpoints-principales)
- [Persistencia de datos: justificación de cada mecanismo](#persistencia-de-datos-justificación-de-cada-mecanismo)
- [Migraciones (Flyway)](#migraciones-flyway)
- [Datos de ejemplo (seed)](#datos-de-ejemplo-seed)
- [Posibles mejoras futuras](#posibles-mejoras-futuras)

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Java 21** | Lenguaje |
| **Spring Boot 4** | Framework base (Spring Web MVC, inyección de dependencias) |
| **Spring Data JPA** | Acceso a datos y repositorios |
| **Hibernate** | Implementación JPA (ORM) |
| **PostgreSQL 16** | Base de datos relacional |
| **Flyway** | Migraciones versionadas del esquema y datos |
| **Bean Validation** | Validación de datos de entrada |
| **springdoc-openapi (Swagger UI)** | Documentación interactiva de la API |
| **Lombok** | Reducción de código repetitivo (getters/setters, constructores) |
| **Maven** (con *Maven Wrapper*) | Construcción del proyecto |
| **Docker Compose** | PostgreSQL en contenedor |

---

## Cómo ejecutar el proyecto

### Requisitos

- **JDK 21** (o superior; se compila apuntando a Java 21).
- **Docker** (para levantar PostgreSQL). No se necesita instalar Maven: el proyecto
  incluye el *Maven Wrapper* (`mvnw`).

### Pasos

**1. Levantar la base de datos** (PostgreSQL en Docker):

```bash
docker compose up -d
```

Esto crea la base `insight_events` con usuario `insight` / contraseña `insight123`
en el puerto `5432` (ver `docker-compose.yml`).

**2. Ejecutar la aplicación**:

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows (PowerShell)
.\mvnw.cmd spring-boot:run
```

Al arrancar, **Flyway** ejecuta automáticamente todas las migraciones (esquema +
datos de ejemplo). La API queda disponible en `http://localhost:8080`.

**3. Verificar que está viva**:

```bash
curl http://localhost:8080/actuator/health
```

> **Reiniciar la BD desde cero:** `docker compose down -v` y luego `docker compose up -d`
> (borra el volumen; Flyway vuelve a crear todo, incluidos los datos de ejemplo).

### Configuración

La conexión y el comportamiento de JPA/Flyway están en
`src/main/resources/application.properties`. Se usa `spring.jpa.hibernate.ddl-auto=validate`
(el esquema lo gestiona Flyway; Hibernate solo valida que las entidades coincidan).

---

## Documentación de la API (Swagger)

Con la aplicación corriendo:

- **Swagger UI** (interfaz para probar los endpoints): `http://localhost:8080/swagger-ui.html`
- **Especificación OpenAPI (JSON)**: `http://localhost:8080/v3/api-docs`

> Las operaciones de escritura aceptan un header opcional **`X-Usuario`** para registrar
> en el historial quién realiza la acción (ver [decisiones de diseño](#arquitectura-y-decisiones-de-diseño)).

---

## Arquitectura y decisiones de diseño

### Arquitectura por capas

```
Controller  →  Service  →  Repository  →  PostgreSQL
   (DTOs)       (lógica)     (entidades)
        Mapper · Exception · Config (transversales)
```

Estructura de paquetes (`com.insightevents.events_api`):

| Paquete | Responsabilidad |
|---|---|
| `controller` | Endpoints REST. Validan la entrada y delegan; sin lógica de negocio. |
| `service` (+ `impl`) | Lógica de negocio y transacciones. Interfaz separada de la implementación. |
| `repository` (+ `projection`) | Acceso a datos (Spring Data JPA, JPQL, SQL nativo, stored procedure). |
| `domain` (+ `enums`) | Entidades JPA y enumeraciones. |
| `dto` | Contratos de la API (request/response). |
| `mapper` | Conversión entidad ↔ DTO. |
| `exception` | Manejo global de errores. |
| `config` | Configuración transversal (OpenAPI). |

**Por qué esta separación:** cada capa tiene una única responsabilidad, el dominio no
se expone en la API (se usan DTOs), y el código es fácil de testear y mantener.

### Decisiones clave

- **DTOs + Mappers:** las entidades JPA nunca se exponen en la API. Los DTOs desacoplan
  el contrato HTTP del esquema de la base de datos, evitan problemas de serialización de
  relaciones *lazy* y permiten aplicar validaciones de entrada donde corresponde.

- **Manejo global de errores** (`@RestControllerAdvice`): todas las excepciones se traducen
  a respuestas HTTP consistentes (`404`, `409`, `400`) con un cuerpo de error uniforme
  (`ApiError`: timestamp, status, error, message, path y detalle de validación). Ningún
  controlador contiene `try/catch`.

- **Flyway con `ddl-auto=validate`** (en vez de que Hibernate genere el esquema): permite
  **versionar el esquema**, hacerlo **reproducible** en cualquier entorno y, sobre todo,
  incluir objetos que Hibernate no puede generar (como el **stored procedure**). Hibernate
  queda como red de seguridad: si una entidad y su tabla se desincronizan, la app no arranca.

- **Borrado lógico (soft delete) de eventos:** eliminar un evento no lo borra físicamente;
  marca `activo = false`. Al ser una plataforma de **auditoría**, la eliminación debe quedar
  registrada y ser reversible, no hacer desaparecer el rastro. Las consultas filtran por
  `activo = true`.

- **Integridad referencial con `ON DELETE RESTRICT`:** no se puede eliminar una categoría con
  eventos, ni un analista con asignaciones. Se valida proactivamente en el servicio (respuesta
  `409` con mensaje claro) y se respalda con la restricción de la llave foránea; además hay un
  manejador de `DataIntegrityViolationException` como red de seguridad.

- **Estados controlados por acciones, no por `update`:** el estado de un evento no se cambia
  con un `PUT` genérico, sino mediante acciones dedicadas (asignar, resolver, cancelar). Esto
  garantiza transiciones válidas y coherentes.

- **Identidad del usuario vía header `X-Usuario`:** como la prueba no requiere autenticación,
  el usuario responsable de cada acción (para el historial) se toma de un header en lugar de
  montar un módulo de seguridad completo. Es una solución proporcional al alcance.

---

## Modelo de datos

Cinco entidades:

```
              ┌─────────────┐
              │  Categoria  │ 1 ── N  ┌───────────────────────────┐
              └─────────────┘         │           Evento          │
                                      │ (soft delete: campo activo)│
              ┌─────────────┐         └───────────────────────────┘
              │  Analista   │              │ 1              │ 1
              └─────────────┘              │ N              │ N
                   │ 1              ┌───────────────┐  ┌──────────────────┐
                   │ N             │   Asignacion   │  │ HistorialEvento  │
                   └───────────────│ (evento+analista│  │ (auditoría de     │
                                   │  únicos)        │  │  acciones)        │
                                   └───────────────┘  └──────────────────┘
```

- **Categoria** `(id, nombre único, descripcion)`
- **Analista** `(id, nombre, correo único)`
- **Evento** `(id, codigo único, titulo, descripcion, fecha, prioridad, estado, fuente, categoria, activo)`
- **Asignacion** `(id, evento, analista, fechaAsignacion, estado)` — **restricción única `(evento, analista)`** para evitar asignaciones duplicadas.
- **HistorialEvento** `(id, evento, usuario, fecha, accion, comentario)`

**Enumeraciones** (persistidas como texto, `@Enumerated(STRING)`, para que sean legibles y
estables ante reordenamientos):

- `Prioridad`: BAJA, MEDIA, ALTA, CRITICA
- `EstadoEvento`: NUEVO, EN_INVESTIGACION, RESUELTO, DESCARTADO
- `EstadoAsignacion`: ACTIVA, FINALIZADA, CANCELADA
- `TipoAccion`: CREACION, ACTUALIZACION, ELIMINACION, ASIGNACION, CAMBIO_ESTADO

Un evento **puede asignarse a varios analistas** (relación N:M a través de `Asignacion`); lo
que se impide es asignar el mismo evento al mismo analista dos veces.

---

## Ciclo de vida de un evento

Los estados se controlan mediante acciones:

```
   crear            asignar              resolver
  ───────►  NUEVO ─────────► EN_INVESTIGACION ─────────► RESUELTO
                                   │
                                   │ cancelar (desde cualquier estado abierto)
                                   ▼
                              DESCARTADO
```

- **Asignar** un evento lo pasa a `EN_INVESTIGACION`.
- **Resolver** una asignación pasa el evento a `RESUELTO` y **finaliza todas** sus asignaciones
  activas (incluidas las de otros analistas), evitando asignaciones activas sobre un evento ya
  resuelto. El historial registra quién lo resolvió.
- **Cancelar** un evento lo pasa a `DESCARTADO` y sus asignaciones activas a `CANCELADA`.

Cada una de estas acciones queda registrada automáticamente en el historial.

---

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| **Categorías** | | |
| POST | `/api/categorias` | Crear |
| GET | `/api/categorias/{id}` | Consultar |
| GET | `/api/categorias` | Listar |
| PUT | `/api/categorias/{id}` | Editar |
| DELETE | `/api/categorias/{id}` | Eliminar (409 si tiene eventos) |
| **Analistas** | | |
| POST/GET/PUT/DELETE | `/api/analistas...` | CRUD completo |
| GET | `/api/analistas/{id}/eventos` | Eventos asignados al analista (con id de asignación, paginado) |
| **Eventos** | | |
| POST | `/api/eventos` | Crear |
| GET | `/api/eventos/{id}` | Consultar |
| GET | `/api/eventos` | Buscar con filtros + paginación |
| PUT | `/api/eventos/{id}` | Editar (no cambia el estado) |
| DELETE | `/api/eventos/{id}` | Eliminar (borrado lógico) |
| POST | `/api/eventos/{id}/cancelar` | Cancelar → DESCARTADO |
| **Asignaciones** | | |
| GET | `/api/eventos/{eventoId}/asignaciones` | Asignaciones de un evento |
| POST | `/api/eventos/{eventoId}/asignaciones` | Asignar (stored procedure) |
| POST | `/api/eventos/{eventoId}/asignaciones/{asignacionId}/resolver` | Resolver → evento RESUELTO |
| GET | `/api/asignaciones` | Todas las asignaciones (paginado) |
| **Historial** | | |
| GET | `/api/eventos/{eventoId}/historial` | Historial completo de un evento |
| **Reportes** | | |
| GET | `/api/reportes/carga-analistas` | Carga de trabajo por analista |
| GET | `/api/reportes/eventos-sin-asignar` | Eventos abiertos pendientes de asignar |

**Búsqueda de eventos** (`GET /api/eventos`) admite filtros opcionales combinables:
`categoriaId`, `estado`, `prioridad`, `fechaDesde`, `fechaHasta`, `texto` (busca en título y
descripción), más paginación (`page`, `size`, `sort`).

---

## Persistencia de datos: justificación de cada mecanismo

Uno de los objetivos de la prueba es demostrar dominio de distintos mecanismos de acceso a
datos. La regla que seguimos: **usar el mecanismo más adecuado para cada caso**.

### 1. Spring Data JPA — métodos derivados (uso selectivo)

Se usan **solo** para consultas simples y directas, donde el nombre del método expresa la
intención sin ambigüedad:

- `existsByNombre`, `existsByNombreAndIdNot` (unicidad de categoría)
- `existsByCorreo`, `existsByCorreoAndIdNot` (unicidad de correo del analista)
- `findByIdAndActivoTrue` (respetar el soft delete)
- `existsByCategoriaId`, `existsByAnalistaId` (validar integridad antes de borrar)
- `findByEventoIdAndEstado`, `findByEventoId`

**Por qué:** para estos casos son la opción más legible y concisa; no aportaría nada escribir
JPQL a mano.

### 2. JPQL (consultas orientadas a objetos)

Se usan cuando la consulta tiene lógica que los métodos derivados no expresan bien (filtros
dinámicos, subconsultas, agregación, orden):

- **Búsqueda de eventos con filtros dinámicos + paginación** (`EventoRepository.buscar`): un solo
  query cubre todas las combinaciones de filtros mediante el patrón `:param IS NULL OR campo = :param`.
- **Historial de un evento** ordenado por fecha (`HistorialEventoRepository.historialDeEvento`).
- **Eventos sin asignar** con `NOT EXISTS` sobre `Asignacion` (`EventoRepository.eventosSinAsignar`).
- **Asignaciones activas de un analista** (`AsignacionRepository.asignacionesActivasDeAnalista`).

**Por qué:** JPQL trabaja sobre entidades (no tablas), es independiente del motor de base de
datos y expresa con claridad las relaciones (`e.categoria.id`, `NOT EXISTS`, etc.).

### 3. SQL nativo

Se baja a SQL cuando se necesita algo específico del motor o un control fino:

- **Secuencia del código de evento** (`EventoRepository.siguienteValorCodigo`):
  `SELECT nextval('evento_codigo_seq')` para generar códigos legibles `EVT-000001`.
- **Reporte de carga por analista** (`AsignacionRepository.cargaPorAnalista`): un `LEFT JOIN`
  con agregación que incluye a los analistas con 0 asignaciones, mapeado con una *projection de
  interfaz*.

**Por qué:** `nextval` es propio de PostgreSQL; y el reporte con `LEFT JOIN` + `COUNT` se
expresa de forma más directa en SQL cuando el objetivo es un resultado agregado, no entidades.

### 4. Stored procedure (asignación)

La asignación de un evento a un analista se resuelve con un **procedimiento almacenado**
(`asignar_evento`, en `V5`) que, en **una sola operación atómica dentro de la base de datos**:

1. valida que el evento exista (y esté activo),
2. valida que el analista exista,
3. evita asignaciones duplicadas,
4. crea la asignación,
5. registra la acción en el historial.

Ante un error, lanza una excepción con un **SQLSTATE propio** (`EV404`, `AN404`, `DUP09`) que la
aplicación traduce a la respuesta HTTP adecuada (`404` / `409`) en `AsignacionServiceImpl`.

**Por qué:** encapsular esta operación crítica en la base de datos garantiza **atomicidad**
(o se hace todo, o nada), la ejecuta cerca de los datos y ofrece las mismas garantías a
cualquier cliente que la invoque, no solo a esta aplicación.

---

## Migraciones (Flyway)

El esquema y los datos se construyen de forma versionada en `src/main/resources/db/migration`:

| Migración | Contenido |
|---|---|
| `V1` | Tablas `categoria` y `analista` |
| `V2` | Tabla `evento` (FK a categoría, enums, soft delete, índices) |
| `V3` | Secuencia `evento_codigo_seq` para el código del evento |
| `V4` | Tablas `asignacion` (única evento+analista) e `historial_evento` |
| `V5` | Stored procedure `asignar_evento` |
| `V6` | Datos de ejemplo (seed) |

---

## Datos de ejemplo (seed)

La migración `V6` carga un escenario de demostración: **4 categorías, 3 analistas y 7 eventos**
que cubren todos los estados (NUEVO, EN_INVESTIGACION, RESUELTO, DESCARTADO), incluyendo un
evento **multi-analista**, uno resuelto y uno descartado, con sus asignaciones e historial
coherentes. Así la API puede probarse de inmediato (por ejemplo desde Swagger UI).

---

## Posibles mejoras futuras

- **Autenticación y autorización** (Spring Security): reemplazar el header `X-Usuario` por un
  usuario autenticado obtenido del token; permitiría además controlar permisos por rol.
- **Filtros dinámicos con JPA Specifications (Criteria API):** cuando la búsqueda crezca en
  número de filtros, las *Specifications* construyen la consulta condicionalmente en Java y
  evitan el patrón `:param IS NULL OR ...`.
- **Orden por severidad de prioridad:** hoy el enum se guarda como texto y un `ORDER BY prioridad`
  ordenaría alfabéticamente; se podría mapear la prioridad a un valor numérico para ordenar por
  severidad real.
- **MapStruct:** generar los *mappers* automáticamente en tiempo de compilación para reducir
  código repetitivo.
- **Validación de correo insensible a mayúsculas:** normalizar el correo del analista a
  minúsculas para tratar `Correo@x.com` y `correo@x.com` como el mismo.
- **Unicidad del analista por nombre:** actualmente el **correo** es único (validado en el
  servicio y con restricción en la base de datos), ya que es el identificador natural de una
  persona. El **nombre no** es único a propósito, porque dos personas distintas pueden llamarse
  igual. Si el negocio lo requiriera, podría añadirse una validación adicional por nombre (o por
  la combinación nombre + correo), asumiendo esa regla.
- **Evolución de enums:** al eliminar un valor de enum, acompañarlo de una migración que
  actualice los datos existentes que lo usaban (evita filas "huérfanas" con un valor ya no
  reconocido por el código).
- **Pruebas automatizadas** (unitarias y de integración con Testcontainers) y **despliegue**
  público de la API.
