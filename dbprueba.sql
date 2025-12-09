-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for dbprueba
CREATE DATABASE IF NOT EXISTS `dbprueba` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dbprueba`;

-- Dumping structure for table dbprueba.centro_costo
CREATE TABLE IF NOT EXISTS `centro_costo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.centro_costo: ~3 rows (approximately)
INSERT INTO `centro_costo` (`id`, `codigo`, `nombre`, `estado`) VALUES
	(1, '2000', 'Administración y Finanzas', 'Activo'),
	(2, '3130', 'Urbanismo', 'Activo'),
	(3, '3000', 'Operaciones', 'Activo');

-- Dumping structure for table dbprueba.detalle_orden
CREATE TABLE IF NOT EXISTS `detalle_orden` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_orden_compra` int NOT NULL,
  `id_detalle_requerimiento` int NOT NULL,
  `cantidad` decimal(10,2) NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `importe` decimal(12,2) GENERATED ALWAYS AS ((`cantidad` * `precio_unitario`)) STORED,
  PRIMARY KEY (`id`),
  KEY `id_orden_compra` (`id_orden_compra`),
  KEY `id_detalle_requerimiento` (`id_detalle_requerimiento`),
  CONSTRAINT `detalle_orden_ibfk_1` FOREIGN KEY (`id_orden_compra`) REFERENCES `orden_compra` (`id`),
  CONSTRAINT `detalle_orden_ibfk_2` FOREIGN KEY (`id_detalle_requerimiento`) REFERENCES `detalle_requerimiento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.detalle_orden: ~0 rows (approximately)

-- Dumping structure for table dbprueba.detalle_requerimiento
CREATE TABLE IF NOT EXISTS `detalle_requerimiento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_requerimiento` int NOT NULL,
  `id_material` int NOT NULL,
  `cantidad_solicitada` decimal(10,2) NOT NULL,
  `cantidad_atendida` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `id_requerimiento` (`id_requerimiento`),
  KEY `id_material` (`id_material`),
  CONSTRAINT `detalle_requerimiento_ibfk_1` FOREIGN KEY (`id_requerimiento`) REFERENCES `requerimiento` (`id`),
  CONSTRAINT `detalle_requerimiento_ibfk_2` FOREIGN KEY (`id_material`) REFERENCES `material` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.detalle_requerimiento: ~0 rows (approximately)

-- Dumping structure for table dbprueba.grupo
CREATE TABLE IF NOT EXISTS `grupo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codgrupo` char(3) NOT NULL,
  `nombre` varchar(85) NOT NULL,
  `descripcion` varchar(85) DEFAULT NULL,
  `observacion` varchar(85) DEFAULT NULL,
  `estado` enum('Activo','Baja') DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `codgrupo` (`codgrupo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.grupo: ~0 rows (approximately)
INSERT INTO `grupo` (`id`, `codgrupo`, `nombre`, `descripcion`, `observacion`, `estado`) VALUES
	(1, 'CON', 'Construcción', 'Materiales de obra gruesa', NULL, 'Activo');

-- Dumping structure for table dbprueba.material
CREATE TABLE IF NOT EXISTS `material` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codcorrelativo` char(3) DEFAULT NULL,
  `nombre` varchar(75) NOT NULL,
  `fecha_creacion` date DEFAULT NULL,
  `estado` enum('Activo','Baja') DEFAULT 'Activo',
  `idgrupo` int DEFAULT NULL,
  `idunidad` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_material_grupo` (`idgrupo`),
  KEY `fk_material_unidad` (`idunidad`),
  CONSTRAINT `fk_material_grupo` FOREIGN KEY (`idgrupo`) REFERENCES `grupo` (`id`),
  CONSTRAINT `fk_material_unidad` FOREIGN KEY (`idunidad`) REFERENCES `unidad` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.material: ~2 rows (approximately)
INSERT INTO `material` (`id`, `codcorrelativo`, `nombre`, `fecha_creacion`, `estado`, `idgrupo`, `idunidad`) VALUES
	(1, '001', 'ALAMBRE NEGRO RECOCIDO N 16', '2025-12-08', 'Activo', 1, 1),
	(2, '002', 'CLAVOS PARA MADERA', '2025-12-08', 'Activo', 1, 1),
	(3, '001', 'ALAMBRA GALVANIZADO N 16', '2025-12-08', 'Activo', 1, 1);

-- Dumping structure for table dbprueba.orden_compra
CREATE TABLE IF NOT EXISTS `orden_compra` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_orden` varchar(20) DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_entrega` date DEFAULT NULL,
  `moneda` enum('Soles','Dolares') DEFAULT 'Soles',
  `forma_pago` varchar(50) DEFAULT NULL,
  `igv` decimal(10,2) DEFAULT '18.00',
  `total` decimal(12,2) DEFAULT NULL,
  `id_proveedor` int NOT NULL,
  `id_usuario_compras` int NOT NULL,
  `estado` enum('Generada','Enviada','Anulada') DEFAULT 'Generada',
  PRIMARY KEY (`id`),
  KEY `id_proveedor` (`id_proveedor`),
  KEY `id_usuario_compras` (`id_usuario_compras`),
  CONSTRAINT `orden_compra_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id`),
  CONSTRAINT `orden_compra_ibfk_2` FOREIGN KEY (`id_usuario_compras`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.orden_compra: ~0 rows (approximately)

-- Dumping structure for table dbprueba.proveedor
CREATE TABLE IF NOT EXISTS `proveedor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ruc` varchar(11) NOT NULL,
  `razon_social` varchar(150) NOT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `cuenta_bancaria` varchar(50) DEFAULT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ruc` (`ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.proveedor: ~2 rows (approximately)
INSERT INTO `proveedor` (`id`, `ruc`, `razon_social`, `direccion`, `telefono`, `contacto`, `correo`, `cuenta_bancaria`, `estado`) VALUES
	(1, '10428481850', 'MIX SA', 'Carretera Panamericana Sur Km 17.5', NULL, NULL, NULL, NULL, 'Activo'),
	(2, '20100000001', 'FERRETERIA CENTRAL SAC', 'Av. Industrial 500', NULL, NULL, NULL, NULL, 'Activo');

-- Dumping structure for table dbprueba.proyecto
CREATE TABLE IF NOT EXISTS `proyecto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) DEFAULT NULL,
  `nombre` varchar(150) DEFAULT NULL,
  `ubicacion` varchar(150) DEFAULT NULL,
  `estado` enum('Activo','Finalizado') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.proyecto: ~2 rows (approximately)
INSERT INTO `proyecto` (`id`, `codigo`, `nombre`, `ubicacion`, `estado`) VALUES
	(1, 'PRJ-01', 'Residencial Los Alamos', 'Av. Central 123', 'Activo'),
	(2, 'PRJ-02', 'Carretera Norte', 'Km 45 Panamericana', 'Activo');

-- Dumping structure for table dbprueba.requerimiento
CREATE TABLE IF NOT EXISTS `requerimiento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_req` varchar(20) DEFAULT NULL,
  `fecha_solicitud` date NOT NULL,
  `observacion` text,
  `id_usuario_solicitante` int NOT NULL,
  `id_proyecto` int NOT NULL,
  `id_centro_costo` int NOT NULL,
  `estado` enum('Pendiente','Observado','Aprobado','En Atencion','Atendido Total','Rechazado') DEFAULT 'Pendiente',
  PRIMARY KEY (`id`),
  KEY `id_usuario_solicitante` (`id_usuario_solicitante`),
  KEY `id_proyecto` (`id_proyecto`),
  KEY `id_centro_costo` (`id_centro_costo`),
  CONSTRAINT `requerimiento_ibfk_1` FOREIGN KEY (`id_usuario_solicitante`) REFERENCES `usuario` (`id`),
  CONSTRAINT `requerimiento_ibfk_2` FOREIGN KEY (`id_proyecto`) REFERENCES `proyecto` (`id`),
  CONSTRAINT `requerimiento_ibfk_3` FOREIGN KEY (`id_centro_costo`) REFERENCES `centro_costo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.requerimiento: ~0 rows (approximately)

-- Dumping structure for table dbprueba.unidad
CREATE TABLE IF NOT EXISTS `unidad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `abreviatura` varchar(30) DEFAULT NULL,
  `descripcion` varchar(50) DEFAULT NULL,
  `estado` enum('Activo','Baja') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.unidad: ~3 rows (approximately)
INSERT INTO `unidad` (`id`, `abreviatura`, `descripcion`, `estado`) VALUES
	(1, 'KG', 'KILOGRAMO', 'Activo'),
	(2, 'UND', 'UNIDAD', 'Activo'),
	(3, 'VAR', 'VARILLA', 'Activo');

-- Dumping structure for table dbprueba.usuario
CREATE TABLE IF NOT EXISTS `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nombre_completo` varchar(100) DEFAULT NULL,
  `cargo` enum('ENCARGADO_OBRA','JEFE_AREA','LOGISTICA','ADMINISTRADOR') NOT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table dbprueba.usuario: ~4 rows (approximately)
INSERT INTO `usuario` (`id`, `username`, `password`, `nombre_completo`, `cargo`, `estado`) VALUES
	(1, 'juan.obra', '1234', 'Juan Perez (Obrero)', 'ENCARGADO_OBRA', 'Activo'),
	(2, 'luis.jefe', '1234', 'Luis Martinez (Jefe)', 'JEFE_AREA', 'Activo'),
	(3, 'ana.logistica', '1234', 'Ana Lopez (Compras)', 'LOGISTICA', 'Activo'),
	(4, 'admin', 'admin123', 'Administrador del Sistema', 'ADMINISTRADOR', 'Activo');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
