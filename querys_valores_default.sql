INSERT INTO presentacion (nombre)
SELECT 'GENERAL'
WHERE NOT EXISTS (
    SELECT 1
    FROM presentacion
    WHERE LOWER(nombre) = LOWER('GENERAL')
);