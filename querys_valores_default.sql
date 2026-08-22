INSERT INTO presentacion (nombre, activo)
SELECT 'GENERAL', TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM presentacion
    WHERE LOWER(nombre) = LOWER('GENERAL')
);