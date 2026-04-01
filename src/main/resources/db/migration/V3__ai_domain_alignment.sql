-- AI domain alignment (gastos + ahorro rich contract)
SET @schema_name = DATABASE();

-- tbl_gastos: idCategoria, idPresupuesto
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_gastos' AND column_name='idCategoria'),
  'SELECT 1',
  'ALTER TABLE tbl_gastos ADD COLUMN idCategoria INT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_gastos' AND column_name='idPresupuesto'),
  'SELECT 1',
  'ALTER TABLE tbl_gastos ADD COLUMN idPresupuesto INT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- tbl_ahorro: new Android-rich contract
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND column_name='idUsuario'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD COLUMN idUsuario INT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND column_name='nombreObjetivo'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD COLUMN nombreObjetivo VARCHAR(120) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND column_name='descripcionObjetivo'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD COLUMN descripcionObjetivo TEXT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND column_name='meta'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD COLUMN meta DECIMAL(10,2) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND column_name='montoAhorrado'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD COLUMN montoAhorrado DECIMAL(10,2) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND column_name='fechaLimite'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD COLUMN fechaLimite DATE NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- backfill data
UPDATE tbl_ahorro a
LEFT JOIN tbl_ingresos i ON i.idIngresos = a.idIngresos
SET a.idUsuario = COALESCE(a.idUsuario, i.idUsuario),
    a.nombreObjetivo = COALESCE(a.nombreObjetivo, 'Meta de ahorro'),
    a.meta = COALESCE(a.meta, a.montoAhorro, 0),
    a.montoAhorrado = COALESCE(a.montoAhorrado, a.montoAhorro, 0)
WHERE a.idAhorro IS NOT NULL;

-- foreign key + indexes
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_gastos' AND index_name='idx_tbl_gastos_user_date_cat'),
  'SELECT 1',
  'CREATE INDEX idx_tbl_gastos_user_date_cat ON tbl_gastos(idUsuario, fechaGastos, idCategoria)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND index_name='idx_tbl_ahorro_usuario'),
  'SELECT 1',
  'CREATE INDEX idx_tbl_ahorro_usuario ON tbl_ahorro(idUsuario)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_schema=@schema_name AND table_name='tbl_ahorro' AND constraint_name='fk_tbl_ahorro_usuario'),
  'SELECT 1',
  'ALTER TABLE tbl_ahorro ADD CONSTRAINT fk_tbl_ahorro_usuario FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
