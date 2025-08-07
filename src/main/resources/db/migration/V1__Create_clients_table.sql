-- =====================================================
-- Migración V1: Crear tabla de clientes
-- =====================================================
-- Descripción: Crea la tabla principal de clientes con
-- todos los campos requeridos, índices y restricciones.
-- 
-- Autor: Sistema de Desarrollo
-- Fecha: 2024
-- =====================================================

-- Crear tabla de clientes
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL COMMENT 'Nombre del cliente',
    last_name VARCHAR(100) NOT NULL COMMENT 'Apellido del cliente',
    age INT NOT NULL COMMENT 'Edad del cliente',
    birth_date DATE NOT NULL COMMENT 'Fecha de nacimiento',
    created_at DATE NOT NULL COMMENT 'Fecha de creación del registro',
    updated_at DATE COMMENT 'Fecha de última actualización',
    
    -- Restricciones de validación (sin funciones no determinísticas)
    CONSTRAINT chk_age_positive CHECK (age > 0),
    CONSTRAINT chk_age_reasonable CHECK (age <= 150),
    CONSTRAINT chk_names_not_empty CHECK (
        TRIM(first_name) != '' AND TRIM(last_name) != ''
    )
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tabla principal de clientes del sistema';

-- Índices para optimizar consultas
CREATE INDEX idx_clients_first_name ON clients(first_name);
CREATE INDEX idx_clients_last_name ON clients(last_name);
CREATE INDEX idx_clients_age ON clients(age);
CREATE INDEX idx_clients_birth_date ON clients(birth_date);
CREATE INDEX idx_clients_created_at ON clients(created_at);

-- Índice compuesto para búsquedas por nombre completo
CREATE INDEX idx_clients_full_name ON clients(first_name, last_name);

-- Índice compuesto para consultas estadísticas por edad
CREATE INDEX idx_clients_age_stats ON clients(age, created_at);

-- =====================================================
-- Datos de ejemplo para testing (opcional)
-- =====================================================
-- Insertar algunos clientes de ejemplo para pruebas
INSERT INTO clients (first_name, last_name, age, birth_date, created_at, updated_at) VALUES
('Juan', 'Pérez', 30, '1994-03-15', CURDATE(), CURDATE()),
('María', 'González', 25, '1999-07-22', CURDATE(), CURDATE()),
('Carlos', 'Rodríguez', 35, '1989-11-08', CURDATE(), CURDATE()),
('Ana', 'Martínez', 28, '1996-05-12', CURDATE(), CURDATE()),
('Luis', 'García', 42, '1982-09-03', CURDATE(), CURDATE()),
('Carmen', 'López', 33, '1991-01-25', CURDATE(), CURDATE()),
('Miguel', 'Sánchez', 27, '1997-04-18', CURDATE(), CURDATE()),
('Isabel', 'Fernández', 39, '1985-12-07', CURDATE(), CURDATE()),
('Diego', 'Torres', 31, '1993-08-14', CURDATE(), CURDATE()),
('Laura', 'Ruiz', 26, '1998-02-28', CURDATE(), CURDATE());

-- =====================================================
-- Comentarios sobre el diseño
-- =====================================================
/*
Decisiones de diseño tomadas:

1. **Eliminación de CHECK constraint para birth_date**:
   - MySQL no permite funciones no determinísticas en CHECK constraints
   - La validación de fecha se hace en la capa de aplicación (Bean Validation)

2. **Validaciones en aplicación**:
   - @Past en la entidad Client valida que birth_date sea en el pasado
   - Validaciones adicionales en el service layer

3. **Tipo de datos**:
   - BIGINT para ID: Permite hasta 9 quintillones de registros
   - VARCHAR(100): Suficiente para nombres en la mayoría de culturas
   - INT para edad: Eficiente y permite validaciones
   - DATE para fechas: Precisión de día es suficiente

4. **Índices**:
   - Índices individuales para consultas frecuentes
   - Índice compuesto para búsquedas por nombre completo
   - Índice para optimizar cálculos estadísticos

5. **Charset**:
   - utf8mb4: Soporte completo para Unicode incluyendo emojis
   - Collation unicode_ci: Comparaciones insensibles a mayúsculas

6. **Engine**:
   - InnoDB: Soporte para transacciones y claves foráneas
*/