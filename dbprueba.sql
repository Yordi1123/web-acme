-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Database:                     dbprueba
-- Updated:                      2025-12-09 - ACME ERP v2
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS `dbprueba`;
CREATE DATABASE IF NOT EXISTS `dbprueba` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `dbprueba`;

-- ============================================================
-- TABLA: area_negocio (NUEVA)
-- ============================================================
CREATE TABLE IF NOT EXISTS `area_negocio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `prefijo` varchar(5) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `id_jefe` int DEFAULT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: centro_costo
-- ============================================================
CREATE TABLE IF NOT EXISTS `centro_costo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4;

INSERT INTO `centro_costo` (`id`, `codigo`, `nombre`, `estado`) VALUES
	(1, '2000', 'Administracion y Finanzas', 'Activo'),
	(2, '2100', 'Contabilidad', 'Activo'),
	(3, '2200', 'Tesoreria', 'Activo'),
	(4, '2300', 'RRHH', 'Activo'),
	(5, '2400', 'Administracion', 'Activo'),
	(6, '2500', 'Legal', 'Activo'),
	(7, '3000', 'Operaciones', 'Activo'),
	(8, '3110', 'Planta', 'Activo'),
	(9, '3120', 'Planta Concreto', 'Activo'),
	(10, '3130', 'Urbanismo', 'Activo'),
	(11, '3140', 'Mantenimiento', 'Activo'),
	(12, '3150', 'Proyectos', 'Activo'),
	(13, '3160', 'Logistica', 'Activo'),
	(14, '4000', 'Comercial', 'Activo');

-- ============================================================
-- TABLA: unidad
-- ============================================================
CREATE TABLE IF NOT EXISTS `unidad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `abreviatura` varchar(30) DEFAULT NULL,
  `descripcion` varchar(50) DEFAULT NULL,
  `estado` enum('Activo','Baja') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

INSERT INTO `unidad` (`id`, `abreviatura`, `descripcion`, `estado`) VALUES
	(1, 'KG', 'KILOGRAMO', 'Activo'),
	(2, 'UND', 'UNIDAD', 'Activo'),
	(3, 'VAR', 'VARILLA', 'Activo'),
	(4, 'M', 'METRO', 'Activo'),
	(5, 'M2', 'METRO CUADRADO', 'Activo'),
	(6, 'M3', 'METRO CUBICO', 'Activo'),
	(7, 'BLS', 'BOLSA', 'Activo'),
	(8, 'GAL', 'GALON', 'Activo'),
	(9, 'PZA', 'PIEZA', 'Activo');

-- ============================================================
-- TABLA: grupo
-- ============================================================
CREATE TABLE IF NOT EXISTS `grupo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codgrupo` char(3) NOT NULL,
  `nombre` varchar(85) NOT NULL,
  `descripcion` varchar(85) DEFAULT NULL,
  `observacion` varchar(85) DEFAULT NULL,
  `estado` enum('Activo','Baja') DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `codgrupo` (`codgrupo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

INSERT INTO `grupo` (`id`, `codgrupo`, `nombre`, `descripcion`, `observacion`, `estado`) VALUES
	(1, 'CON', 'Construccion', 'Materiales de obra gruesa', NULL, 'Activo'),
	(2, 'FER', 'Ferreteria', 'Clavos, tornillos, herramientas', NULL, 'Activo'),
	(3, 'ELE', 'Electrico', 'Cables, interruptores, tableros', NULL, 'Activo'),
	(4, 'SAN', 'Sanitario', 'Tuberias, valvulas, accesorios', NULL, 'Activo'),
	(5, 'ACA', 'Acabados', 'Pinturas, cerámicos, enchapes', NULL, 'Activo');

-- ============================================================
-- TABLA: material
-- ============================================================
CREATE TABLE IF NOT EXISTS `material` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codcorrelativo` char(3) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `fecha_creacion` date DEFAULT NULL,
  `estado` enum('Activo','Baja') DEFAULT 'Activo',
  `idgrupo` int DEFAULT NULL,
  `idunidad` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_material_grupo` (`idgrupo`),
  KEY `fk_material_unidad` (`idunidad`),
  CONSTRAINT `fk_material_grupo` FOREIGN KEY (`idgrupo`) REFERENCES `grupo` (`id`),
  CONSTRAINT `fk_material_unidad` FOREIGN KEY (`idunidad`) REFERENCES `unidad` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4;

INSERT INTO `material` (`id`, `codcorrelativo`, `nombre`, `fecha_creacion`, `estado`, `idgrupo`, `idunidad`) VALUES
	-- GRUPO CONSTRUCCION
	(1, '001', 'ALAMBRE NEGRO RECOCIDO N16', '2025-01-01', 'Activo', 1, 1),
	(2, '002', 'CEMENTO PORTLAND TIPO I', '2025-01-01', 'Activo', 1, 7),
	(3, '003', 'ARENA FINA', '2025-01-01', 'Activo', 1, 6),
	(4, '004', 'ARENA GRUESA', '2025-01-01', 'Activo', 1, 6),
	(5, '005', 'PIEDRA CHANCADA 1/2', '2025-01-01', 'Activo', 1, 6),
	(6, '006', 'PIEDRA CHANCADA 3/4', '2025-01-01', 'Activo', 1, 6),
	(7, '007', 'LADRILLO KING KONG 18H', '2025-01-01', 'Activo', 1, 2),
	(8, '008', 'FIERRO CORRUGADO 1/2', '2025-01-01', 'Activo', 1, 3),
	(9, '009', 'FIERRO CORRUGADO 3/8', '2025-01-01', 'Activo', 1, 3),
	(10, '010', 'FIERRO CORRUGADO 5/8', '2025-01-01', 'Activo', 1, 3),
	-- GRUPO FERRETERIA
	(11, '001', 'CLAVO 2 PULGADAS', '2025-01-01', 'Activo', 2, 1),
	(12, '002', 'CLAVO 3 PULGADAS', '2025-01-01', 'Activo', 2, 1),
	(13, '003', 'CLAVO 4 PULGADAS', '2025-01-01', 'Activo', 2, 1),
	(14, '004', 'BISAGRA 3 PULGADAS', '2025-01-01', 'Activo', 2, 9),
	(15, '005', 'CERRADURA TIPO POMO', '2025-01-01', 'Activo', 2, 2),
	-- GRUPO ELECTRICO
	(16, '001', 'CABLE THW 14 AWG', '2025-01-01', 'Activo', 3, 4),
	(17, '002', 'CABLE THW 12 AWG', '2025-01-01', 'Activo', 3, 4),
	(18, '003', 'INTERRUPTOR SIMPLE', '2025-01-01', 'Activo', 3, 2),
	(19, '004', 'TOMACORRIENTE DOBLE', '2025-01-01', 'Activo', 3, 2),
	(20, '005', 'CAJA RECTANGULAR PVC', '2025-01-01', 'Activo', 3, 2),
	-- GRUPO SANITARIO
	(21, '001', 'TUBO PVC 4 PULGADAS', '2025-01-01', 'Activo', 4, 2),
	(22, '002', 'TUBO PVC 2 PULGADAS', '2025-01-01', 'Activo', 4, 2),
	(23, '003', 'CODO PVC 4x90', '2025-01-01', 'Activo', 4, 2),
	(24, '004', 'TEE PVC 4 PULGADAS', '2025-01-01', 'Activo', 4, 2),
	(25, '005', 'PEGAMENTO PVC 1/4 GAL', '2025-01-01', 'Activo', 4, 8),
	-- GRUPO ACABADOS
	(26, '001', 'PINTURA LATEX BLANCO', '2025-01-01', 'Activo', 5, 8),
	(27, '002', 'PINTURA LATEX MARFIL', '2025-01-01', 'Activo', 5, 8),
	(28, '003', 'CERAMICO 45X45 BEIGE', '2025-01-01', 'Activo', 5, 5),
	(29, '004', 'FRAGUA BLANCA', '2025-01-01', 'Activo', 5, 1),
	(30, '005', 'PEGAMENTO CERAMICO', '2025-01-01', 'Activo', 5, 7);

-- ============================================================
-- TABLA: usuario (ACTUALIZADA con id_area_negocio)
-- ============================================================
CREATE TABLE IF NOT EXISTS `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nombre_completo` varchar(100) DEFAULT NULL,
  `cargo` enum('ENCARGADO_OBRA','JEFE_AREA','EMPLEADO_COMPRAS','ADMINISTRADOR') NOT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  `id_area_negocio` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuario_area` (`id_area_negocio`),
  CONSTRAINT `fk_usuario_area` FOREIGN KEY (`id_area_negocio`) REFERENCES `area_negocio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: proveedor
-- ============================================================
CREATE TABLE IF NOT EXISTS `proveedor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ruc` varchar(11) NOT NULL,
  `razon_social` varchar(200) NOT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `cuenta_bancaria` varchar(50) DEFAULT NULL,
  `estado` enum('Activo','Inactivo') DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ruc` (`ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

INSERT INTO `proveedor` (`id`, `ruc`, `razon_social`, `direccion`, `telefono`, `contacto`, `correo`, `cuenta_bancaria`, `estado`) VALUES
	(1, '20100000001', 'CEMENTOS LIMA SAC', 'Av. Atocongo 2440, Lima', '01-5124000', 'Juan Ventas', 'ventas@cementoslima.com', NULL, 'Activo'),
	(2, '20100000002', 'ACEROS AREQUIPA SA', 'Av. Argentina 5679, Lima', '01-5174000', 'Maria Comercial', 'comercial@acerosarequipa.com', NULL, 'Activo'),
	(3, '20100000003', 'FERRETERIA CENTRAL SAC', 'Jr. Lampa 456, Lima', '01-4234567', 'Pedro Atencion', 'atencion@ferreteriacentral.com', NULL, 'Activo'),
	(4, '20100000004', 'ELECTRO PERU EIRL', 'Av. Industrial 789, Lima', '01-4561234', 'Carlos Ventas', 'ventas@electroperu.com', NULL, 'Activo'),
	(5, '20100000005', 'SANITARIOS DEL SUR SAC', 'Calle Los Pinos 123, Arequipa', '054-234567', 'Ana Ventas', 'ventas@sanitariossur.com', NULL, 'Activo');

-- ============================================================
-- TABLA: proyecto
-- ============================================================
CREATE TABLE IF NOT EXISTS `proyecto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) DEFAULT NULL,
  `nombre` varchar(150) DEFAULT NULL,
  `ubicacion` varchar(150) DEFAULT NULL,
  `estado` enum('Activo','Finalizado') DEFAULT 'Activo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

INSERT INTO `proyecto` (`id`, `codigo`, `nombre`, `ubicacion`, `estado`) VALUES
	(1, 'PRJ-001', 'Residencial Los Alamos', 'Av. Central 123, Surco', 'Activo'),
	(2, 'PRJ-002', 'Carretera Norte Tramo 1', 'Km 45 Panamericana Norte', 'Activo'),
	(3, 'PRJ-003', 'Centro Comercial Plaza Sur', 'Av. Primavera 1234, San Borja', 'Activo'),
	(4, 'PRJ-004', 'Edificio Torre Azul', 'Jr. Las Flores 567, Miraflores', 'Activo'),
	(5, 'PRJ-005', 'Condominio Vista Mar', 'Malecón Grau 890, Chorrillos', 'Activo'),
	(6, 'PRJ-006', 'Puente Peatonal Universitario', 'Av. Universitaria 2500', 'Activo'),
	(7, 'PRJ-007', 'Parque Industrial Los Heroes', 'Zona Industrial Sur', 'Activo'),
	(8, 'PRJ-008', 'Hospital Regional Norte', 'Av. Tupac Amaru Km 12', 'Activo'),
	(9, 'PRJ-009', 'Colegio Nacional Nuevo Peru', 'Av. Separadora Industrial', 'Activo'),
	(10, 'PRJ-010', 'Ampliacion Planta Concreto', 'Zona Industrial Este', 'Activo');

-- ============================================================
-- DATOS: area_negocio con jefes
-- ============================================================
INSERT INTO `area_negocio` (`id`, `prefijo`, `nombre`, `id_jefe`, `estado`) VALUES
	(1, 'PC', 'Produccion/Urbanismo', NULL, 'Activo'),
	(2, 'AR', 'Administracion Regional', NULL, 'Activo'),
	(3, 'AL', 'Administracion Central', NULL, 'Activo'),
	(4, 'AC', 'Atencion al Cliente', NULL, 'Activo'),
	(5, 'VC', 'Ventas/Credito y Cobranza', NULL, 'Activo'),
	(6, 'MA', 'Mantenimiento', NULL, 'Activo'),
	(7, 'MK', 'Marketing', NULL, 'Activo'),
	(8, 'OS', 'Operaciones y Servicios', NULL, 'Activo'),
	(9, 'SE', 'Seguridad', NULL, 'Activo');

-- ============================================================
-- DATOS: usuarios (Jefes, Encargados, Empleados Compras)
-- ============================================================
INSERT INTO `usuario` (`id`, `username`, `password`, `nombre_completo`, `cargo`, `estado`, `id_area_negocio`) VALUES
	-- JEFES DE AREA
  (1, 'miguel.angulo', '1234', 'Miguel Angulo Rios', 'JEFE_AREA', 'Activo', 1),
	(2, 'ana.lopez', '1234', 'Ana Lopez Gutierrez', 'JEFE_AREA', 'Activo', 2),
	(3, 'luis.martinez', '1234', 'Luis Martinez Perez', 'JEFE_AREA', 'Activo', 3),
	(4, 'eva.perez', '1234', 'Eva Perez Sanchez', 'JEFE_AREA', 'Activo', 4),
	(5, 'carlos.ames', '1234', 'Carlos Ames Diaz', 'JEFE_AREA', 'Activo', 5),
	(14, 'fausto.mantenimiento', '1234', 'Fausto Rivera Torres', 'JEFE_AREA', 'Activo', 6),
	(15, 'gloria.marketing', '1234', 'Gloria Mendez Soto', 'JEFE_AREA', 'Activo', 7),
	(16, 'hector.operaciones', '1234', 'Hector Vargas Luna', 'JEFE_AREA', 'Activo', 8),
	(17, 'irene.seguridad', '1234', 'Irene Campos Rodriguez', 'JEFE_AREA', 'Activo', 9),
	-- ENCARGADOS DE OBRA
	(6, 'juan.obra1', '1234', 'Juan Perez Castro', 'ENCARGADO_OBRA', 'Activo', 1),
	(7, 'pedro.obra2', '1234', 'Pedro Gomez Luna', 'ENCARGADO_OBRA', 'Activo', 1),
	(8, 'maria.obra3', '1234', 'Maria Torres Ruiz', 'ENCARGADO_OBRA', 'Activo', 2),
	(9, 'roberto.obra4', '1234', 'Roberto Silva Vargas', 'ENCARGADO_OBRA', 'Activo', 3),
	(10, 'carmen.obra5', '1234', 'Carmen Rojas Mendez', 'ENCARGADO_OBRA', 'Activo', 6),
	-- EMPLEADOS DE COMPRAS
	(11, 'compras1', '1234', 'Sofia Vega Campos', 'EMPLEADO_COMPRAS', 'Activo', NULL),
	(12, 'compras2', '1234', 'Diego Fernandez Soto', 'EMPLEADO_COMPRAS', 'Activo', NULL),
	-- ADMINISTRADOR
	(13, 'admin', 'admin123', 'Administrador del Sistema', 'ADMINISTRADOR', 'Activo', NULL);

-- Actualizar jefes en area_negocio
UPDATE `area_negocio` SET `id_jefe` = 1 WHERE `id` = 1;
UPDATE `area_negocio` SET `id_jefe` = 2 WHERE `id` = 2;
UPDATE `area_negocio` SET `id_jefe` = 3 WHERE `id` = 3;
UPDATE `area_negocio` SET `id_jefe` = 4 WHERE `id` = 4;
UPDATE `area_negocio` SET `id_jefe` = 5 WHERE `id` = 5;
UPDATE `area_negocio` SET `id_jefe` = 14 WHERE `id` = 6;
UPDATE `area_negocio` SET `id_jefe` = 15 WHERE `id` = 7;
UPDATE `area_negocio` SET `id_jefe` = 16 WHERE `id` = 8;
UPDATE `area_negocio` SET `id_jefe` = 17 WHERE `id` = 9;

-- ============================================================
-- TABLA: orden_compra (con campos adicionales)
-- ============================================================
CREATE TABLE IF NOT EXISTS `orden_compra` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_orden` varchar(20) DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_entrega` date DEFAULT NULL,
  `moneda` enum('Soles','Dolares') DEFAULT 'Soles',
  `forma_pago` varchar(50) DEFAULT NULL,
  `sub_total` decimal(12,2) DEFAULT NULL,
  `igv` decimal(10,2) DEFAULT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `id_proveedor` int NOT NULL,
  `id_usuario_compras` int NOT NULL,
  `estado` enum('Borrador','Generada','Aprobada','Enviada','Anulada') DEFAULT 'Borrador',
  `lugar_entrega` varchar(200) DEFAULT NULL,
  `fecha_entrega_almacen` date DEFAULT NULL,
  `observaciones` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_proveedor` (`id_proveedor`),
  KEY `id_usuario_compras` (`id_usuario_compras`),
  CONSTRAINT `orden_compra_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id`),
  CONSTRAINT `orden_compra_ibfk_2` FOREIGN KEY (`id_usuario_compras`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: requerimiento (con campos nuevos)
-- ============================================================
CREATE TABLE IF NOT EXISTS `requerimiento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_req` varchar(20) DEFAULT NULL,
  `fecha_solicitud` date NOT NULL,
  `observacion` text,
  `id_usuario_solicitante` int NOT NULL,
  `id_proyecto` int NOT NULL,
  `id_centro_costo` int NOT NULL,
  `estado` enum('PENDIENTE','OBSERVADO','APROBADO','EN_ATENCION','ATENDIDO_TOTAL','RECHAZADO') DEFAULT 'PENDIENTE',
  `etapa` int DEFAULT NULL,
  `id_area_negocio` int DEFAULT NULL,
  `id_jefe_aprobador` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_usuario_solicitante` (`id_usuario_solicitante`),
  KEY `id_proyecto` (`id_proyecto`),
  KEY `id_centro_costo` (`id_centro_costo`),
  KEY `id_area_negocio` (`id_area_negocio`),
  KEY `id_jefe_aprobador` (`id_jefe_aprobador`),
  CONSTRAINT `requerimiento_ibfk_1` FOREIGN KEY (`id_usuario_solicitante`) REFERENCES `usuario` (`id`),
  CONSTRAINT `requerimiento_ibfk_2` FOREIGN KEY (`id_proyecto`) REFERENCES `proyecto` (`id`),
  CONSTRAINT `requerimiento_ibfk_3` FOREIGN KEY (`id_centro_costo`) REFERENCES `centro_costo` (`id`),
  CONSTRAINT `requerimiento_ibfk_4` FOREIGN KEY (`id_area_negocio`) REFERENCES `area_negocio` (`id`),
  CONSTRAINT `requerimiento_ibfk_5` FOREIGN KEY (`id_jefe_aprobador`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: detalle_requerimiento
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: detalle_orden
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
