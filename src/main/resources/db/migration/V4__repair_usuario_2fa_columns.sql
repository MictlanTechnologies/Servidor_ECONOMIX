SET @schema_name = DATABASE();

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema=@schema_name
          AND table_name='tbl_usuario'
          AND column_name='twoFactorEnabled'
    ),
    'SELECT 1',
    'ALTER TABLE tbl_usuario ADD COLUMN twoFactorEnabled TINYINT(1) NOT NULL DEFAULT 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema=@schema_name
          AND table_name='tbl_usuario'
          AND column_name='twoFactorSecretEncrypted'
    ),
    'SELECT 1',
    'ALTER TABLE tbl_usuario ADD COLUMN twoFactorSecretEncrypted VARCHAR(1000) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema=@schema_name
          AND table_name='tbl_usuario'
          AND column_name='twoFactorVerifiedAt'
    ),
    'SELECT 1',
    'ALTER TABLE tbl_usuario ADD COLUMN twoFactorVerifiedAt TIMESTAMP NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema=@schema_name
          AND table_name='tbl_usuario'
          AND column_name='lastOtpTimestepUsed'
    ),
    'SELECT 1',
    'ALTER TABLE tbl_usuario ADD COLUMN lastOtpTimestepUsed BIGINT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
