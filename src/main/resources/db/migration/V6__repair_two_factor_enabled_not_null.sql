SET @schema_name = DATABASE();

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = @schema_name
          AND table_name = 'tbl_usuario'
          AND column_name = 'twoFactorEnabled'
    ),
    'UPDATE tbl_usuario SET twoFactorEnabled = 0 WHERE twoFactorEnabled IS NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = @schema_name
          AND table_name = 'tbl_usuario'
          AND column_name = 'twoFactorEnabled'
    ),
    'ALTER TABLE tbl_usuario MODIFY COLUMN twoFactorEnabled TINYINT(1) NOT NULL DEFAULT 0',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
