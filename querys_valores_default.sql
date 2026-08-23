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
INSERT INTO tarifa_iva
    (nombre, tipo, porcentaje, activo)
VALUES
    ('IVA GENERAL 19%', 'GRAVADO', 19.00, TRUE),
    ('IVA REDUCIDO 5%', 'GRAVADO', 5.00, TRUE),
    ('EXENTO', 'EXENTO', 0.00, TRUE),
    ('EXCLUIDO', 'EXCLUIDO', 0.00, TRUE);