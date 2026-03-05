# ECONOMIX Auth JWT + Refresh + 2FA (TOTP)

## Flujo para frontend
1. `POST /auth/login` con usuario+password.
2. Si responde `requires2fa=false`, usar `accessToken` y `refreshToken` normalmente.
3. Si responde `requires2fa=true`, **abrir TwoFactorActivity** y enviar `challengeId` a pantalla OTP.
4. `POST /auth/2fa/verify` con `challengeId` y `otpCode`.
5. Guardar `refreshToken` en almacenamiento seguro del dispositivo.
6. Al abrir app nuevamente, si `accessToken` expiró, llamar `POST /auth/refresh`.
7. Solo llamar `POST /auth/logout` cuando usuario presione **Salir**.

## Ejemplos API

### POST /auth/login
Request
```json
{
  "usernameOrEmail": "demo@economix.com",
  "password": "123456"
}
```

Response cuando NO requiere 2FA
```json
{
  "requires2fa": false,
  "accessToken": "eyJ...",
  "refreshToken": "Q3Y...",
  "userInfo": {
    "userId": 12,
    "username": "demo@economix.com",
    "roles": ["USER"],
    "twoFactorEnabled": false
  }
}
```

Response cuando SÍ requiere 2FA
```json
{
  "requires2fa": true,
  "challengeId": "6d4d0b6f-4a25-4d69-a4c4-4defece7a0bc",
  "challengeExpiresAt": "2026-01-15T20:01:00"
}
```

### POST /auth/2fa/verify
```json
{
  "challengeId": "6d4d0b6f-4a25-4d69-a4c4-4defece7a0bc",
  "otpCode": "123456"
}
```

### POST /auth/refresh
```json
{
  "refreshToken": "Q3Y..."
}
```

Response
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "n3w..."
}
```

### POST /auth/logout
```json
{
  "refreshToken": "n3w..."
}
```
Response: `204 No Content`

### POST /users/2fa/setup (Bearer token requerido)
Response
```json
{
  "otpauthUri": "otpauth://totp/ECONOMIX:DEMO%40ECONOMIX.COM?secret=ABCDEF&issuer=ECONOMIX",
  "secretMasked": "ABCD****WXYZ"
}
```

### POST /users/2fa/enable
```json
{
  "otpCode": "123456"
}
```

### POST /users/2fa/disable
```json
{
  "otpCode": "123456"
}
```

## Seguridad aplicada
- Verificación OTP solo en backend.
- Ventana TOTP ±1 timestep (30s).
- Anti-replay con `lastOtpTimestepUsed`.
- Rate limiting en `/auth/login` y `/auth/2fa/verify` por IP + usuario/challenge.
- Secreto 2FA cifrado con AES-GCM (`ECONOMIX_2FA_KEY`).
- Refresh token persistido solo como SHA-256 hash.
- JWT con `userId`, `roles`, `iat`, `exp` (15 min).
- Refresh token con expiración de 30 días y rotación en `/auth/refresh`.

## Recomendaciones de despliegue
- Configurar HTTPS obligatorio en gateway/proxy y backend.
- Configurar CORS para solo los orígenes de ECONOMIX app/web.
- No habilitar logs de bodies en endpoints auth.
