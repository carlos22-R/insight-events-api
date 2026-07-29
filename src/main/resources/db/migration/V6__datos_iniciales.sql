-- =====================================================================
-- V6: Datos iniciales (seed) - escenario de demostracion.
-- Cubre todos los estados del evento y casos representativos
-- (multi-analista, evento resuelto, evento descartado, sin asignar).
-- Textos sin acentos ni enie para evitar problemas de codificacion.
-- =====================================================================

-- ===== Categorias =====
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Transito',  'Accidentes e incidentes de transito'),
    ('Clima',     'Reportes y alertas meteorologicas'),
    ('Seguridad', 'Incidentes de seguridad publica'),
    ('Salud',     'Emergencias y alertas sanitarias');

-- ===== Analistas =====
INSERT INTO analista (nombre, correo) VALUES
    ('Maria Lopez', 'maria@insight.com'),
    ('Juan Perez',  'juan@insight.com'),
    ('Ana Torres',  'ana@insight.com');

-- ===== Eventos (uno por cada estado) =====
INSERT INTO evento (codigo, titulo, descripcion, fecha, prioridad, estado, fuente, categoria_id, activo) VALUES
    ('EVT-000001','Choque en autopista norte','Colision de 3 vehiculos, trafico detenido','2026-07-20 08:15:00','ALTA',   'NUEVO',            'Camara de transito 45', (SELECT id FROM categoria WHERE nombre='Transito'),  TRUE),
    ('EVT-000002','Lluvia intensa zona centro','Alerta meteorologica por tormenta',        '2026-07-21 14:30:00','MEDIA',  'NUEVO',            'Sensor meteorologico',  (SELECT id FROM categoria WHERE nombre='Clima'),     TRUE),
    ('EVT-000003','Volcadura en km 40','Vehiculo pesado volcado, via bloqueada',           '2026-07-22 06:50:00','CRITICA','EN_INVESTIGACION', 'Llamada 911',           (SELECT id FROM categoria WHERE nombre='Transito'),  TRUE),
    ('EVT-000004','Amenaza en evento masivo','Reporte de objeto sospechoso en estadio',    '2026-07-23 19:00:00','ALTA',   'EN_INVESTIGACION', 'Reporte ciudadano',     (SELECT id FROM categoria WHERE nombre='Seguridad'), TRUE),
    ('EVT-000005','Brote sanitario zona sur','Casos de intoxicacion alimentaria',          '2026-07-18 11:20:00','MEDIA',  'RESUELTO',         'Hospital regional',     (SELECT id FROM categoria WHERE nombre='Salud'),     TRUE),
    ('EVT-000006','Reporte de humo','Humo confundido con incendio (falsa alarma)',         '2026-07-19 16:45:00','BAJA',   'DESCARTADO',       'Sistema de alarmas',    (SELECT id FROM categoria WHERE nombre='Clima'),     TRUE),
    ('EVT-000007','Semaforo con falla cruce 5','Semaforo intermitente, riesgo vial',       '2026-07-24 09:10:00','MEDIA',  'NUEVO',            'Reporte ciudadano',     (SELECT id FROM categoria WHERE nombre='Transito'),  TRUE);

-- La secuencia del codigo continua en EVT-000008 para los eventos que cree la app
SELECT setval('evento_codigo_seq', 7);

-- ===== Asignaciones =====
INSERT INTO asignacion (evento_id, analista_id, fecha_asignacion, estado) VALUES
    -- EVT-000003: en investigacion por Maria
    ((SELECT id FROM evento WHERE codigo='EVT-000003'), (SELECT id FROM analista WHERE correo='maria@insight.com'), '2026-07-22 07:00:00','ACTIVA'),
    -- EVT-000004: multi-analista (Juan y Ana)
    ((SELECT id FROM evento WHERE codigo='EVT-000004'), (SELECT id FROM analista WHERE correo='juan@insight.com'),  '2026-07-23 19:30:00','ACTIVA'),
    ((SELECT id FROM evento WHERE codigo='EVT-000004'), (SELECT id FROM analista WHERE correo='ana@insight.com'),   '2026-07-23 19:35:00','ACTIVA'),
    -- EVT-000005: resuelto (asignacion finalizada)
    ((SELECT id FROM evento WHERE codigo='EVT-000005'), (SELECT id FROM analista WHERE correo='maria@insight.com'), '2026-07-18 12:00:00','FINALIZADA');

-- ===== Historial (coherente con estados y asignaciones) =====
INSERT INTO historial_evento (evento_id, usuario, fecha, accion, comentario) VALUES
    -- EVT-000001 (solo creado)
    ((SELECT id FROM evento WHERE codigo='EVT-000001'),'sistema',   '2026-07-20 08:15:00','CREACION',     'Evento creado con codigo EVT-000001'),
    -- EVT-000003 (creado + asignado)
    ((SELECT id FROM evento WHERE codigo='EVT-000003'),'sistema',   '2026-07-22 06:50:00','CREACION',     'Evento creado con codigo EVT-000003'),
    ((SELECT id FROM evento WHERE codigo='EVT-000003'),'supervisor','2026-07-22 07:00:00','ASIGNACION',   'Evento asignado a Maria Lopez'),
    -- EVT-000004 (creado + 2 asignaciones)
    ((SELECT id FROM evento WHERE codigo='EVT-000004'),'sistema',   '2026-07-23 19:00:00','CREACION',     'Evento creado con codigo EVT-000004'),
    ((SELECT id FROM evento WHERE codigo='EVT-000004'),'supervisor','2026-07-23 19:30:00','ASIGNACION',   'Evento asignado a Juan Perez'),
    ((SELECT id FROM evento WHERE codigo='EVT-000004'),'supervisor','2026-07-23 19:35:00','ASIGNACION',   'Evento asignado a Ana Torres'),
    -- EVT-000005 (creado + asignado + resuelto)
    ((SELECT id FROM evento WHERE codigo='EVT-000005'),'sistema',   '2026-07-18 11:20:00','CREACION',     'Evento creado con codigo EVT-000005'),
    ((SELECT id FROM evento WHERE codigo='EVT-000005'),'supervisor','2026-07-18 12:00:00','ASIGNACION',   'Evento asignado a Maria Lopez'),
    ((SELECT id FROM evento WHERE codigo='EVT-000005'),'maria',     '2026-07-18 17:30:00','CAMBIO_ESTADO','Evento resuelto por maria'),
    -- EVT-000006 (creado + descartado)
    ((SELECT id FROM evento WHERE codigo='EVT-000006'),'sistema',   '2026-07-19 16:45:00','CREACION',     'Evento creado con codigo EVT-000006'),
    ((SELECT id FROM evento WHERE codigo='EVT-000006'),'supervisor','2026-07-19 17:00:00','CAMBIO_ESTADO','Evento cancelado (DESCARTADO)');
