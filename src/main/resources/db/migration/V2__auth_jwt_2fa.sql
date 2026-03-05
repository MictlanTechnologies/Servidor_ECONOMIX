-- JWT + Refresh Token + TOTP 2FA
ALTER TABLE tbl_usuario
    ADD COLUMN twoFactorEnabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN twoFactorSecretEncrypted VARCHAR(512) NULL,
    ADD COLUMN twoFactorVerifiedAt TIMESTAMP NULL,
    ADD COLUMN lastOtpTimestepUsed BIGINT NULL;

CREATE TABLE IF NOT EXISTS tbl_refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    tokenHash VARCHAR(64) NOT NULL UNIQUE,
    createdAt TIMESTAMP NOT NULL,
    expiresAt TIMESTAMP NOT NULL,
    revokedAt TIMESTAMP NULL,
    deviceInfo VARCHAR(255) NULL,
    CONSTRAINT fk_refresh_user FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
);

CREATE TABLE IF NOT EXISTS tbl_auth_challenge (
    id VARCHAR(64) PRIMARY KEY,
    idUsuario INT NOT NULL,
    expiresAt TIMESTAMP NOT NULL,
    consumedAt TIMESTAMP NULL,
    CONSTRAINT fk_challenge_user FOREIGN KEY (idUsuario) REFERENCES tbl_usuario(idUsuario)
);
