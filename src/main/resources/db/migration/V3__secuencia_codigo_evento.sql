-- =====================================================================
-- V3: Secuencia para generar el codigo legible del evento (EVT-000001)
-- =====================================================================
-- Se usa desde la aplicacion (nextval) para asignar un codigo secuencial
-- y unico a cada evento nuevo, independiente del id tecnico.

CREATE SEQUENCE evento_codigo_seq START WITH 1 INCREMENT BY 1;
