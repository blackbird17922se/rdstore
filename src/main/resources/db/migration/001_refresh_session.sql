-- MySQL 8.0. Ejecute UNA vez antes de arrancar con ddl-auto=validate.
CREATE TABLE IF NOT EXISTS refresh_session (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at DATETIME(6) NOT NULL,
    revoked BIT(1) NOT NULL DEFAULT b'0',
    INDEX idx_refresh_session_usuario (id_usuario),
    INDEX idx_refresh_session_expiry (expires_at),
    CONSTRAINT fk_refresh_session_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- Nota: si Usuario usa otra tabla/PK, adaptar la FK antes de ejecutar.
