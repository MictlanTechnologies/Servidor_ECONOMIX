# Flyway recovery: failed migration V2 (auth jwt 2fa)

## Por qué pasó
Flyway detectó una migración `V2__auth_jwt_2fa.sql` parcialmente aplicada (fallida). En ese estado, `flyway_schema_history` registra versión `2` con `success = 0`, y Spring Boot detiene el arranque para evitar inconsistencias.

## Recuperación local (orden exacto)
1. **Haz respaldo** de tu base `economix` desde MySQL Workbench.
2. Ejecuta el script manual:
   - `src/main/resources/sql/recover_failed_flyway_v2.sql`
3. Arranca el backend desde IntelliJ (o `mvn spring-boot:run`).
4. Verifica que Flyway termine correctamente y aplique baseline + V2 sin error.

## Queries de verificación
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
SHOW COLUMNS FROM tbl_usuario;
SHOW TABLES LIKE 'tbl_refresh_token';
SHOW TABLES LIKE 'tbl_auth_challenge';
```

## Resultado esperado
- En `flyway_schema_history` ya no debe existir una fila fallida de versión `2`.
- Debe existir el historial de baseline (`1`/`B1`) exitoso y versión `2` exitosa tras reiniciar.
- `tbl_usuario` debe incluir:
  - `twoFactorEnabled`
  - `twoFactorSecretEncrypted`
  - `twoFactorVerifiedAt`
  - `lastOtpTimestepUsed`
- Deben existir tablas:
  - `tbl_refresh_token`
  - `tbl_auth_challenge`
