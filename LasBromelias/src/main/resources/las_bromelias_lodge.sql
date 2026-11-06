-- Las Bromelias Lodge - MySQL 8.0+
-- Ejecutar este script con un usuario administrador de MySQL.
-- Credenciales de la aplicación (cambiar antes de usar en producción):
-- Usuario: app_bromelias
-- Clave: LbG5!r9Qv#2Nf7xK4pZ8

CREATE DATABASE IF NOT EXISTS las_bromelias_lodge
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'app_bromelias'@'localhost'
  IDENTIFIED BY 'LbG5!r9Qv#2Nf7xK4pZ8';

GRANT SELECT, INSERT, UPDATE, DELETE
  ON las_bromelias_lodge.* TO 'app_bromelias'@'localhost';

USE las_bromelias_lodge;

CREATE TABLE IF NOT EXISTS usuario (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  contrasena VARCHAR(255) NOT NULL COMMENT 'Hash BCrypt; nunca almacenar texto plano',
  telefono VARCHAR(20) NULL,
  rol ENUM('ROLE_CLIENT', 'ROLE_RECEPTION', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_CLIENT',
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuario_correo (correo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cabana (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT NULL,
  num_habitaciones INT UNSIGNED NULL,
  descripcion_camas VARCHAR(255) NULL,
  capacidad INT UNSIGNED NOT NULL,
  precio_crc DECIMAL(10,2) NOT NULL,
  precio_usd DECIMAL(10,2) NOT NULL,
  foto_url VARCHAR(500) NULL,
  estado ENUM('ACTIVA', 'INACTIVA') NOT NULL DEFAULT 'ACTIVA',
  PRIMARY KEY (id),
  CONSTRAINT chk_cabana_capacidad CHECK (capacidad > 0),
  CONSTRAINT chk_cabana_precio_crc CHECK (precio_crc >= 0),
  CONSTRAINT chk_cabana_precio_usd CHECK (precio_usd >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS servicio_extra (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255) NULL,
  precio_crc DECIMAL(10,2) NOT NULL,
  precio_usd DECIMAL(10,2) NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  CONSTRAINT chk_servicio_precio_crc CHECK (precio_crc >= 0),
  CONSTRAINT chk_servicio_precio_usd CHECK (precio_usd >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reserva (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(20) NOT NULL,
  usuario_id BIGINT UNSIGNED NOT NULL,
  cabana_id BIGINT UNSIGNED NOT NULL,
  fecha_entrada DATE NOT NULL,
  fecha_salida DATE NOT NULL,
  cantidad_huespedes INT UNSIGNED NOT NULL,
  lleva_mascotas BOOLEAN NOT NULL DEFAULT FALSE,
  moneda CHAR(3) NOT NULL,
  precio_noche_aplicado DECIMAL(10,2) NOT NULL,
  total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  estado ENUM('PENDIENTE_PAGO', 'PAGO_POR_VERIFICAR', 'CONFIRMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'PENDIENTE_PAGO',
  origen ENUM('WEB', 'MANUAL', 'BOOKING') NOT NULL DEFAULT 'WEB',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_limite_pago DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_reserva_codigo (codigo),
  KEY ix_reserva_cabana_fechas_estado (cabana_id, fecha_entrada, fecha_salida, estado),
  KEY ix_reserva_usuario_fecha (usuario_id, fecha_creacion),
  CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
  CONSTRAINT fk_reserva_cabana FOREIGN KEY (cabana_id) REFERENCES cabana (id),
  CONSTRAINT chk_reserva_fechas CHECK (fecha_salida > fecha_entrada),
  CONSTRAINT chk_reserva_huespedes CHECK (cantidad_huespedes > 0),
  CONSTRAINT chk_reserva_moneda CHECK (moneda IN ('CRC', 'USD')),
  CONSTRAINT chk_reserva_precios CHECK (precio_noche_aplicado >= 0 AND total >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reserva_servicio (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  reserva_id BIGINT UNSIGNED NOT NULL,
  servicio_id BIGINT UNSIGNED NOT NULL,
  cantidad INT UNSIGNED NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL COMMENT 'Precio congelado en la moneda de la reserva',
  PRIMARY KEY (id),
  UNIQUE KEY uk_reserva_servicio (reserva_id, servicio_id),
  CONSTRAINT fk_reserva_servicio_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id),
  CONSTRAINT fk_reserva_servicio_servicio FOREIGN KEY (servicio_id) REFERENCES servicio_extra (id),
  CONSTRAINT chk_reserva_servicio_cantidad CHECK (cantidad > 0),
  CONSTRAINT chk_reserva_servicio_precio CHECK (precio_unitario >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pago (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  reserva_id BIGINT UNSIGNED NOT NULL,
  metodo ENUM('SINPE', 'EFECTIVO') NOT NULL,
  moneda CHAR(3) NOT NULL,
  numero_comprobante VARCHAR(50) NULL,
  monto DECIMAL(12,2) NOT NULL,
  estado ENUM('POR_VERIFICAR', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'POR_VERIFICAR',
  fecha_reporte DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_verificacion DATETIME NULL,
  registrado_por BIGINT UNSIGNED NULL,
  PRIMARY KEY (id),
  KEY ix_pago_reserva_estado (reserva_id, estado),
  CONSTRAINT fk_pago_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id),
  CONSTRAINT fk_pago_registrado_por FOREIGN KEY (registrado_por) REFERENCES usuario (id),
  CONSTRAINT chk_pago_moneda CHECK (moneda IN ('CRC', 'USD')),
  CONSTRAINT chk_pago_monto CHECK (monto > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS consulta (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  mensaje TEXT NOT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY ix_consulta_fecha (fecha)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bitacora_reserva (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  reserva_id BIGINT UNSIGNED NOT NULL,
  estado_anterior ENUM('PENDIENTE_PAGO', 'PAGO_POR_VERIFICAR', 'CONFIRMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA') NULL,
  estado_nuevo ENUM('PENDIENTE_PAGO', 'PAGO_POR_VERIFICAR', 'CONFIRMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA') NOT NULL,
  usuario_id BIGINT UNSIGNED NULL COMMENT 'NULL cuando el cambio lo realiza el sistema',
  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY ix_bitacora_reserva_fecha (reserva_id, fecha_hora),
  CONSTRAINT fk_bitacora_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id),
  CONSTRAINT fk_bitacora_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
) ENGINE=InnoDB;


--- Datos de prueba

INSERT INTO cabana (nombre, descripcion, num_habitaciones, descripcion_camas, capacidad, precio_crc, precio_usd, foto_url, estado)
SELECT 'Cabaña #1', 'Cabaña con balcon y vistas a la montaña', 2, 'Una cama matrimonial y camas individuales', 6, 45000.00, 90.00, NULL, 'ACTIVA'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM cabana WHERE nombre = 'Cabaña #1');

INSERT INTO cabana (nombre, descripcion, num_habitaciones, descripcion_camas, capacidad, precio_crc, precio_usd, foto_url, estado)
SELECT 'Cabaña #2', 'Cabaña con terraza y senderos.', 2, 'Una cama matrimonial y camas individuales', 6, 45000.00, 90.00, NULL, 'ACTIVA'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM cabana WHERE nombre = 'Cabaña #2');

INSERT INTO usuario (nombre, apellidos, correo, contrasena, telefono, rol, activo, fecha_registro)
VALUES ('Cliente', 'De Prueba', 'cliente@example.com', '$2a$10$66ZrEJZGZd5XtbAGG2YwROzjFGMA7kXApOhMkZ63/qtHQzzSuvL.i', '88880000', 'ROLE_CLIENT', TRUE, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    contrasena = '$2a$10$66ZrEJZGZd5XtbAGG2YwROzjFGMA7kXApOhMkZ63/qtHQzzSuvL.i',
    rol = 'ROLE_CLIENT',
    activo = TRUE;


INSERT INTO reserva (codigo, usuario_id, cabana_id, fecha_entrada, fecha_salida, cantidad_huespedes, lleva_mascotas, moneda, precio_noche_aplicado, total, estado, origen, fecha_creacion, fecha_limite_pago)
SELECT 'RES-PRUEBA-001', (SELECT id FROM usuario WHERE correo = 'cliente@example.com' LIMIT 1), (SELECT id FROM cabana WHERE nombre = 'Cabaña #1' ORDER BY id LIMIT 1), '2026-12-10', '2026-12-13', 4, FALSE, 'CRC', 45000.00, 135000.00, 'CONFIRMADA', 'WEB', CURRENT_TIMESTAMP, NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'RES-PRUEBA-001');

INSERT INTO reserva (codigo, usuario_id, cabana_id, fecha_entrada, fecha_salida, cantidad_huespedes, lleva_mascotas, moneda, precio_noche_aplicado, total, estado, origen, fecha_creacion, fecha_limite_pago)
SELECT 'RES-PRUEBA-002', (SELECT id FROM usuario WHERE correo = 'cliente@example.com' LIMIT 1), (SELECT id FROM cabana WHERE nombre = 'Cabaña #2' ORDER BY id LIMIT 1), '2027-01-15', '2027-01-17', 2, FALSE, 'USD', 90.00, 180.00, 'CONFIRMADA', 'WEB', CURRENT_TIMESTAMP, NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'RES-PRUEBA-002');


