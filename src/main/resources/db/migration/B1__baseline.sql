-- Baseline schema for ECONOMIX (state before JWT/Refresh/2FA)
-- Non-destructive migration for existing databases.

CREATE TABLE IF NOT EXISTS tbl_usuario (
  idUsuario INT NOT NULL AUTO_INCREMENT,
  perfilUsuario VARCHAR(50) NOT NULL,
  `contraseñaUsuario` VARCHAR(100) NOT NULL,
  PRIMARY KEY (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_persona (
  idPersona INT NOT NULL AUTO_INCREMENT,
  nombrePersona VARCHAR(50) NULL DEFAULT NULL,
  apellidoP VARCHAR(50) NULL DEFAULT NULL,
  apellidoM VARCHAR(50) NULL DEFAULT NULL,
  idUsuario INT NULL DEFAULT NULL,
  PRIMARY KEY (idPersona),
  CONSTRAINT tbl_nombreusuario_ibfk_1
    FOREIGN KEY (idUsuario) REFERENCES tbl_usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_contactos (
  idContactos INT NOT NULL AUTO_INCREMENT,
  numCelular VARCHAR(20) NULL DEFAULT NULL,
  Correo VARCHAR(100) NULL DEFAULT NULL,
  idPersona INT NULL DEFAULT NULL,
  PRIMARY KEY (idContactos),
  CONSTRAINT tbl_contactos_ibfk_1
    FOREIGN KEY (idPersona) REFERENCES tbl_persona (idPersona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_domicilio (
  idDomicilio INT NOT NULL AUTO_INCREMENT,
  ciudad VARCHAR(50) NULL DEFAULT NULL,
  calle VARCHAR(100) NULL DEFAULT NULL,
  colonia VARCHAR(100) NULL DEFAULT NULL,
  `número` VARCHAR(10) NULL DEFAULT NULL,
  idPersona INT NULL DEFAULT NULL,
  PRIMARY KEY (idDomicilio),
  CONSTRAINT tbl_domicilio_ibfk_1
    FOREIGN KEY (idPersona) REFERENCES tbl_persona (idPersona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_gastos (
  idGastos INT NOT NULL AUTO_INCREMENT,
  `descripciónGasto` TEXT NOT NULL,
  `artículoGasto` VARCHAR(100) NOT NULL,
  montoGasto DECIMAL(10,2) NOT NULL,
  fechaGastos DATE NOT NULL,
  periodoGastos VARCHAR(50) NOT NULL,
  idUsuario INT NOT NULL,
  PRIMARY KEY (idGastos),
  CONSTRAINT tbl_gastos_ibfk_1
    FOREIGN KEY (idUsuario) REFERENCES tbl_usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_conceptogastos (
  idConcepto INT NOT NULL AUTO_INCREMENT,
  nombreConcepto VARCHAR(100) NULL DEFAULT NULL,
  `descripciónConcepto` TEXT NULL DEFAULT NULL,
  precioConcepto DECIMAL(10,2) NULL DEFAULT NULL,
  idGastos INT NULL DEFAULT NULL,
  PRIMARY KEY (idConcepto),
  CONSTRAINT tbl_conceptogastos_ibfk_1
    FOREIGN KEY (idGastos) REFERENCES tbl_gastos (idGastos)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_ingresos (
  idIngresos INT NOT NULL AUTO_INCREMENT,
  montoIngreso DECIMAL(10,2) NULL DEFAULT NULL,
  periodicidadIngreso VARCHAR(50) NULL DEFAULT NULL,
  fechaIngresos DATE NULL DEFAULT NULL,
  descripcionIngreso TEXT NULL DEFAULT NULL,
  idUsuario INT NULL DEFAULT NULL,
  PRIMARY KEY (idIngresos),
  CONSTRAINT tbl_ingresos_ibfk_1
    FOREIGN KEY (idUsuario) REFERENCES tbl_usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_conceptoingresos (
  idConcepto INT NOT NULL AUTO_INCREMENT,
  nombreConcepto VARCHAR(100) NULL DEFAULT NULL,
  descripcionConcepto TEXT NULL DEFAULT NULL,
  precioConcepto DECIMAL(10,2) NULL DEFAULT NULL,
  idIngresos INT NULL DEFAULT NULL,
  PRIMARY KEY (idConcepto),
  CONSTRAINT tbl_conceptoingresos_ibfk_1
    FOREIGN KEY (idIngresos) REFERENCES tbl_ingresos (idIngresos)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tbl_ahorro (
  idAhorro INT NOT NULL AUTO_INCREMENT,
  fechaAhorro DATE NULL DEFAULT NULL,
  `fechaActualizaciónA` DATE NULL DEFAULT NULL,
  periodoTAhorro VARCHAR(50) NULL DEFAULT NULL,
  montoAhorro DECIMAL(10,2) NULL DEFAULT NULL,
  idIngresos INT NULL DEFAULT NULL,
  PRIMARY KEY (idAhorro),
  CONSTRAINT tbl_ahorro_ibfk_1
    FOREIGN KEY (idIngresos) REFERENCES tbl_ingresos (idIngresos)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Safe index creation for MySQL 8 (no CREATE INDEX IF NOT EXISTS)
SET @schema_name = DATABASE();

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_persona' AND index_name='idUsuario'),
  'SELECT 1',
  'CREATE INDEX idUsuario ON tbl_persona (idUsuario)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_contactos' AND index_name='idPersona'),
  'SELECT 1',
  'CREATE INDEX idPersona ON tbl_contactos (idPersona)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_domicilio' AND index_name='idPersona'),
  'SELECT 1',
  'CREATE INDEX idPersona ON tbl_domicilio (idPersona)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_gastos' AND index_name='idUsuario'),
  'SELECT 1',
  'CREATE INDEX idUsuario ON tbl_gastos (idUsuario)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_conceptogastos' AND index_name='idGastos'),
  'SELECT 1',
  'CREATE INDEX idGastos ON tbl_conceptogastos (idGastos)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_ingresos' AND index_name='idUsuario'),
  'SELECT 1',
  'CREATE INDEX idUsuario ON tbl_ingresos (idUsuario)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_conceptoingresos' AND index_name='idIngresos'),
  'SELECT 1',
  'CREATE INDEX idIngresos ON tbl_conceptoingresos (idIngresos)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND index_name='idIngresos'),
  'SELECT 1',
  'CREATE INDEX idIngresos ON tbl_ahorro (idIngresos)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
