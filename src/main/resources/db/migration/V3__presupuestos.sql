SET @schema_name = DATABASE();

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = @schema_name
          AND table_name = 'tbl_presupuestos'
    ),
    'SELECT 1',
    'CREATE TABLE tbl_presupuestos (
        idPresupuesto INT NOT NULL AUTO_INCREMENT,
        idUsuario INT NOT NULL,
        categoria VARCHAR(100) NOT NULL,
        montoMaximo DECIMAL(10,2) NOT NULL,
        mes INT NOT NULL,
        anio INT NOT NULL,
        createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (idPresupuesto),
        CONSTRAINT fk_presupuesto_usuario FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = @schema_name
          AND table_name = 'tbl_presupuestos'
          AND index_name = 'ux_presupuesto_usuario_categoria_periodo'
    ),
    'SELECT 1',
    'CREATE UNIQUE INDEX ux_presupuesto_usuario_categoria_periodo ON tbl_presupuestos (idUsuario, categoria, mes, anio)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = @schema_name
          AND table_name = 'tbl_presupuestos'
          AND index_name = 'idx_presupuesto_usuario_periodo'
    ),
    'SELECT 1',
    'CREATE INDEX idx_presupuesto_usuario_periodo ON tbl_presupuestos (idUsuario, anio, mes)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = @schema_name
          AND table_name = 'tbl_presupuestos'
          AND index_name = 'idx_presupuesto_categoria'
    ),
    'SELECT 1',
    'CREATE INDEX idx_presupuesto_categoria ON tbl_presupuestos (categoria)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
