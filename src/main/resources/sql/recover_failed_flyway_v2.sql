-- Manual recovery for failed Flyway V2 (JWT + 2FA)
-- Safe for business data: does NOT drop base business tables.

SET @schema_name = DATABASE();

-- 1) Drop V2 tables if present
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='tbl_auth_challenge'),
  'DROP TABLE tbl_auth_challenge',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='tbl_refresh_token'),
  'DROP TABLE tbl_refresh_token',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) Remove V2 columns from tbl_usuario only if they exist
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='twoFactorEnabled'),
  'ALTER TABLE tbl_usuario DROP COLUMN twoFactorEnabled',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='twoFactorSecretEncrypted'),
  'ALTER TABLE tbl_usuario DROP COLUMN twoFactorSecretEncrypted',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='twoFactorVerifiedAt'),
  'ALTER TABLE tbl_usuario DROP COLUMN twoFactorVerifiedAt',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='tbl_usuario' AND column_name='lastOtpTimestepUsed'),
  'ALTER TABLE tbl_usuario DROP COLUMN lastOtpTimestepUsed',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) Delete only failed V2 row from flyway history, if history table exists
SET @sql = IF (
  EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='flyway_schema_history'),
  'DELETE FROM flyway_schema_history WHERE version = ''2'' AND success = 0',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
