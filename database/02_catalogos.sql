INSERT INTO rol (nombre)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1
    FROM rol
    WHERE nombre = 'ADMIN'
);

INSERT INTO rol (nombre)
SELECT 'VENDEDOR'
WHERE NOT EXISTS (
    SELECT 1
    FROM rol
    WHERE nombre = 'VENDEDOR'
);

INSERT INTO categoria (nombre, activo)
SELECT 'GENERAL', TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM categoria
    WHERE LOWER(nombre) = LOWER('GENERAL')
);

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

INSERT INTO tarifa_iva (nombre, tipo, porcentaje, activo)
SELECT 'IVA GENERAL 19%', 'GRAVADO', 19.00, TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM tarifa_iva
    WHERE nombre = 'IVA GENERAL 19%'
);

INSERT INTO tarifa_iva (nombre, tipo, porcentaje, activo)
SELECT 'IVA REDUCIDO 5%', 'GRAVADO', 5.00, TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM tarifa_iva
    WHERE nombre = 'IVA REDUCIDO 5%'
);

INSERT INTO tarifa_iva (nombre, tipo, porcentaje, activo)
SELECT 'EXENTO', 'EXENTO', 0.00, TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM tarifa_iva
    WHERE nombre = 'EXENTO'
);

INSERT INTO tarifa_iva (nombre, tipo, porcentaje, activo)
SELECT 'EXCLUIDO', 'EXCLUIDO', 0.00, TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM tarifa_iva
    WHERE nombre = 'EXCLUIDO'
);


-- ============================================
-- Usuario administrador de demostración
-- Usuario: administrador
-- Contraseña: 123456
-- ============================================

INSERT INTO usuario (
    nombre,
    apellido,
    nombre_usuario,
    contrasena,
    activo,
    id_rol
)
SELECT
    'Administrador',
    'Demo',
    'administrador',
    '$2y$10$1XGYqp1H/FwATz2VQK.3eOfRWBfc5S2eKIThwr8ySgvpCwKIBQGCm',
    TRUE,
    r.id
FROM rol r
WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (
    SELECT 1
    FROM usuario u
    WHERE u.nombre_usuario = 'administrador'
);