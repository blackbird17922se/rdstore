CREATE DATABASE IF NOT EXISTS dstore;
USE dstore;
-- dstore.categoria definition

CREATE TABLE `categoria` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.cliente definition

CREATE TABLE `cliente` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(20) DEFAULT NULL,
  `numero_documento` varchar(30) DEFAULT NULL,
  `nombres_apellidos` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `correo` varchar(150) DEFAULT NULL,
  `direccion` varchar(250) DEFAULT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  `fecha_registro` date NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cliente_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.entrada_inventario definition

CREATE TABLE `entrada_inventario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha_entrada` date NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `numero_documento` varchar(100) DEFAULT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.marca definition

CREATE TABLE `marca` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.presentacion definition

CREATE TABLE `presentacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.proveedor definition

CREATE TABLE `proveedor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.rol definition

CREATE TABLE `rol` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.tarifa_iva definition

CREATE TABLE `tarifa_iva` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `porcentaje` decimal(5,2) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tarifa_iva_nombre` (`nombre`),
  UNIQUE KEY `uk_tarifa_iva_tipo_porcentaje` (`tipo`,`porcentaje`),
  CONSTRAINT `chk_tarifa_iva_porcentaje` CHECK (((`porcentaje` >= 0.00) and (`porcentaje` <= 100.00))),
  CONSTRAINT `chk_tarifa_iva_tipo` CHECK ((`tipo` in (_utf8mb4'GRAVADO',_utf8mb4'EXENTO',_utf8mb4'EXCLUIDO')))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.producto definition

CREATE TABLE `producto` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `precio` decimal(15,2) NOT NULL,
  `id_marca` int DEFAULT NULL,
  `id_tipo` int DEFAULT NULL,
  `id_presentacion` int DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `id_tarifa_iva` bigint NOT NULL,
  `controla_vencimiento` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `id_marca` (`id_marca`),
  KEY `id_tipo` (`id_tipo`),
  KEY `id_presentacion` (`id_presentacion`),
  KEY `fk_producto_tarifa_iva` (`id_tarifa_iva`),
  CONSTRAINT `fk_producto_tarifa_iva` FOREIGN KEY (`id_tarifa_iva`) REFERENCES `tarifa_iva` (`id`),
  CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id`),
  CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`id_tipo`) REFERENCES `categoria` (`id`),
  CONSTRAINT `producto_ibfk_3` FOREIGN KEY (`id_presentacion`) REFERENCES `presentacion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.usuario definition

CREATE TABLE `usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `id_rol` int NOT NULL,
  `nombre_usuario` varchar(255) DEFAULT NULL,
  `activo` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombreUsuario` (`nombre_usuario`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.venta definition

CREATE TABLE `venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `id_cliente` bigint DEFAULT NULL,
  `subtotal` decimal(38,2) DEFAULT NULL,
  `iva_total` decimal(38,2) DEFAULT NULL,
  `total` decimal(38,2) DEFAULT NULL,
  `id_vendedor` bigint NOT NULL,
  `estado` varchar(30) NOT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  `fecha_anulacion` datetime(6) DEFAULT NULL,
  `motivo_anulacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_venta_cliente` (`id_cliente`),
  KEY `fk_venta_vendedor` (`id_vendedor`),
  CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`),
  CONSTRAINT `fk_venta_vendedor` FOREIGN KEY (`id_vendedor`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.detalle_entrada_inventario definition

CREATE TABLE `detalle_entrada_inventario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_entrada` bigint NOT NULL,
  `id_producto` bigint NOT NULL,
  `cantidad` bigint NOT NULL,
  `numero_lote` varchar(100) DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_entrada_entrada` (`id_entrada`),
  KEY `fk_detalle_entrada_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_entrada_entrada` FOREIGN KEY (`id_entrada`) REFERENCES `entrada_inventario` (`id`),
  CONSTRAINT `fk_detalle_entrada_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id`),
  CONSTRAINT `chk_detalle_entrada_cantidad` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.detalle_venta definition

CREATE TABLE `detalle_venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_venta` bigint NOT NULL,
  `id_producto` bigint NOT NULL,
  `cantidad` bigint NOT NULL,
  `precio_unitario` decimal(38,2) DEFAULT NULL,
  `tipo_iva` varchar(20) NOT NULL,
  `porcentaje_iva` decimal(5,2) NOT NULL,
  `subtotal` decimal(38,2) DEFAULT NULL,
  `valor_iva` decimal(15,2) NOT NULL,
  `total` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_venta` (`id_venta`),
  KEY `fk_detalle_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id`),
  CONSTRAINT `fk_detalle_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id`),
  CONSTRAINT `chk_detalle_cantidad` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.existencia_producto definition

CREATE TABLE `existencia_producto` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `cantidad` bigint NOT NULL,
  `numero_lote` varchar(100) DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `fecha_ingreso` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_existencia_producto` (`id_producto`),
  CONSTRAINT `fk_existencia_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id`),
  CONSTRAINT `chk_existencia_cantidad` CHECK ((`cantidad` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.movimiento_inventario definition

CREATE TABLE `movimiento_inventario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_existencia` bigint NOT NULL,
  `tipo` varchar(30) NOT NULL,
  `cantidad` bigint NOT NULL,
  `fecha_movimiento` datetime NOT NULL,
  `tipo_origen` varchar(30) DEFAULT NULL,
  `id_origen` bigint DEFAULT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_movimiento_existencia` (`id_existencia`),
  CONSTRAINT `fk_movimiento_existencia` FOREIGN KEY (`id_existencia`) REFERENCES `existencia_producto` (`id`),
  CONSTRAINT `chk_movimiento_cantidad` CHECK ((`cantidad` <> 0)),
  CONSTRAINT `chk_movimiento_tipo` CHECK ((`tipo` in (_utf8mb4'ENTRADA',_utf8mb4'VENTA',_utf8mb4'AJUSTE_ENTRADA',_utf8mb4'AJUSTE_SALIDA')))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- dstore.ajuste_inventario definition

CREATE TABLE `ajuste_inventario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_existencia` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `cantidad` bigint NOT NULL,
  `motivo` varchar(150) NOT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  `fecha_ajuste` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ajuste_existencia` (`id_existencia`),
  KEY `fk_ajuste_usuario` (`id_usuario`),
  CONSTRAINT `fk_ajuste_existencia` FOREIGN KEY (`id_existencia`) REFERENCES `existencia_producto` (`id`),
  CONSTRAINT `fk_ajuste_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `chk_ajuste_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `chk_ajuste_tipo` CHECK ((`tipo` in (_utf8mb4'ENTRADA',_utf8mb4'SALIDA')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;