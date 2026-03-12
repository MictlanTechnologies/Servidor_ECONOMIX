-- JWT + Refresh Token + TOTP 2FA
-- Idempotent and safe for partially applied states.

SET @schema_name = DATABASE();

-- tbl_usuario columns
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='twoFactorEnabled'),
  'SELECT 1',
  'ALTER TABLE tbl_usuario ADD COLUMN twoFactorEnabled BOOLEAN NOT NULL DEFAULT FALSE'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='twoFactorSecretEncrypted'),
  'SELECT 1',
  'ALTER TABLE tbl_usuario ADD COLUMN twoFactorSecretEncrypted VARCHAR(512) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='twoFactorVerifiedAt'),
  'SELECT 1',
  'ALTER TABLE tbl_usuario ADD COLUMN twoFactorVerifiedAt TIMESTAMP NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='lastOtpTimestepUsed'),
  'SELECT 1',
  'ALTER TABLE tbl_usuario ADD COLUMN lastOtpTimestepUsed BIGINT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- refresh token table
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='tbl_refresh_token'),
  'SELECT 1',
  'CREATE TABLE tbl_refresh_token (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      idUsuario INT NOT NULL,
      tokenHash VARCHAR(64) NOT NULL,
      createdAt TIMESTAMP NOT NULL,
      expiresAt TIMESTAMP NOT NULL,
      revokedAt TIMESTAMP NULL,
      deviceInfo VARCHAR(255) NULL,
      CONSTRAINT fk_refresh_user FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema=@schema_name AND table_name='tbl_refresh_token' AND index_name='ux_tbl_refresh_token_tokenHash'),
  'SELECT 1',
  'CREATE UNIQUE INDEX ux_tbl_refresh_token_tokenHash ON tbl_refresh_token (tokenHash)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- auth challenge table
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='tbl_auth_challenge'),
  'SELECT 1',
  'CREATE TABLE tbl_auth_challenge (
      id VARCHAR(64) PRIMARY KEY,
      idUsuario INT NOT NULL,
      expiresAt TIMESTAMP NOT NULL,
      consumedAt TIMESTAMP NULL,
      CONSTRAINT fk_challenge_user FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
