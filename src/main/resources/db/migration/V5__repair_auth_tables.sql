SET @schema_name = DATABASE();

SET @sql = IF (
    EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='auth_refresh_token'),
    'SELECT 1',
    'CREATE TABLE auth_refresh_token (
      idRefreshToken INT NOT NULL AUTO_INCREMENT,
      idUsuario INT NOT NULL,
      token VARCHAR(255) NOT NULL,
      expiresAt TIMESTAMP NOT NULL,
      revoked TINYINT(1) NOT NULL DEFAULT 0,
      PRIMARY KEY (idRefreshToken),
      UNIQUE KEY ux_refresh_token_token (token),
      KEY idx_refresh_token_usuario (idUsuario),
      CONSTRAINT fk_refresh_token_usuario FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=@schema_name AND table_name='auth_challenge'),
    'SELECT 1',
    'CREATE TABLE auth_challenge (
      idAuthChallenge INT NOT NULL AUTO_INCREMENT,
      challengeId VARCHAR(120) NOT NULL,
      idUsuario INT NOT NULL,
      expiresAt TIMESTAMP NOT NULL,
      used TINYINT(1) NOT NULL DEFAULT 0,
      PRIMARY KEY (idAuthChallenge),
      UNIQUE KEY ux_auth_challenge_id (challengeId),
      KEY idx_auth_challenge_usuario (idUsuario),
      CONSTRAINT fk_auth_challenge_usuario FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
