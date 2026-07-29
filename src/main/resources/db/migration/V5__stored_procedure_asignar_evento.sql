-- =====================================================================
-- V5: Stored procedure de asignacion de un evento a un analista.
--
-- En UNA sola operacion atomica:
--   1. valida que el evento exista (y este activo)
--   2. valida que el analista exista
--   3. evita asignaciones duplicadas
--   4. crea la asignacion
--   5. registra la accion en el historial
--
-- Ante un error, lanza una excepcion con un SQLSTATE propio que la
-- aplicacion traduce a la respuesta HTTP adecuada (404 / 409).
-- =====================================================================

CREATE PROCEDURE asignar_evento(
    p_evento_id   BIGINT,
    p_analista_id BIGINT,
    p_usuario     TEXT,
    p_comentario  TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1. El evento debe existir y estar activo
    IF NOT EXISTS (SELECT 1 FROM evento WHERE id = p_evento_id AND activo = TRUE) THEN
        RAISE EXCEPTION 'El evento % no existe', p_evento_id
            USING ERRCODE = 'EV404';
    END IF;

    -- 2. El analista debe existir
    IF NOT EXISTS (SELECT 1 FROM analista WHERE id = p_analista_id) THEN
        RAISE EXCEPTION 'El analista % no existe', p_analista_id
            USING ERRCODE = 'AN404';
    END IF;

    -- 3. No debe existir ya esa asignacion (evitar duplicados)
    IF EXISTS (SELECT 1 FROM asignacion
               WHERE evento_id = p_evento_id AND analista_id = p_analista_id) THEN
        RAISE EXCEPTION 'El evento % ya esta asignado al analista %', p_evento_id, p_analista_id
            USING ERRCODE = 'DUP09';
    END IF;

    -- 4. Crear la asignacion
    INSERT INTO asignacion (evento_id, analista_id, fecha_asignacion, estado)
    VALUES (p_evento_id, p_analista_id, now(), 'ACTIVA');

    -- 5. Registrar la accion en el historial (automatico)
    INSERT INTO historial_evento (evento_id, usuario, fecha, accion, comentario)
    VALUES (p_evento_id,
            COALESCE(p_usuario, 'sistema'),
            now(),
            'ASIGNACION',
            COALESCE(p_comentario, 'Evento asignado al analista ' || p_analista_id));
END;
$$;
