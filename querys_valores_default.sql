INSERT INTO presentacion (nombre, activo)
SELECT 'GENERAL', TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM presentacion
    WHERE LOWER(nombre) = LOWER('GENERAL')
);
INSERT INTO marca  (nombre, activo)
SELECT 'GENERAL', TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM marca
    WHERE LOWER(nombre) = LOWER('GENERAL')
);