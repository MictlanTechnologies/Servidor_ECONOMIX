package sql.controler;

import sql.dto.LoginRequest;
import sql.dto.TwoFactorQrRequest;
import sql.dto.TwoFactorQrResponse;
import sql.dto.UsuarioDto;
import sql.model.Usuario;
import sql.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private static final String TOTP_ISSUER = "ECONOMIX";
    private static final char[] BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> getAll() {
        List<Usuario> usuarios = usuarioService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getById(@PathVariable Integer id) {
        Usuario usuario = usuarioService.getById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(usuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto) {
        // Validación mínima (evita 500 por nulls)
        if (usuarioDto == null
                || usuarioDto.getPerfilUsuario() == null
                || usuarioDto.getPerfilUsuario().isBlank()
                || usuarioDto.getContrasenaUsuario() == null
                || usuarioDto.getContrasenaUsuario().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Si ya existe un perfil, devolvemos 409 (la app lo maneja como "ya registrado").
        if (usuarioService.existsByPerfilUsuario(usuarioDto.getPerfilUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario usuario = usuarioService.save(toEntity(usuarioDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(usuario));
    }

    /**
     * Login para Android.
     * La app hace POST a /economix/api/usuarios/login con {perfilUsuario, contrasenaUsuario}
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody(required = false) LoginRequest request) {
        if (request == null
                || request.getPerfilUsuario() == null
                || request.getPerfilUsuario().isBlank()
                || request.getContrasenaUsuario() == null
                || request.getContrasenaUsuario().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = usuarioService.findByPerfilUsuario(request.getPerfilUsuario());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Flujo legado compatible con Android actual:
        // Comparación directa (sin hash) porque así está actualmente el proyecto.
        if (!request.getContrasenaUsuario().equals(usuario.getContrasenaUsuario())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(toDto(usuario));
    }

    /**
     * Flujo 2FA legado: genera el contenido para renderizar el QR en Android/Web.
     * No persiste el secreto automáticamente; el cliente debe almacenarlo según su flujo.
     */
    @PostMapping("/2fa/qr")
    public ResponseEntity<TwoFactorQrResponse> generate2faQr(@RequestBody(required = false) TwoFactorQrRequest request) {
        if (request == null || request.getPerfilUsuario() == null || request.getPerfilUsuario().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String perfilUsuario = request.getPerfilUsuario().trim();
        String secret = (request.getSecreto2fa() == null || request.getSecreto2fa().isBlank())
                ? generateBase32Secret(32)
                : request.getSecreto2fa().trim().toUpperCase();

        String label = TOTP_ISSUER + ":" + perfilUsuario;
        String otpauthUrl = "otpauth://totp/"
                + URLEncoder.encode(label, StandardCharsets.UTF_8)
                + "?secret="
                + URLEncoder.encode(secret, StandardCharsets.UTF_8)
                + "&issuer="
                + URLEncoder.encode(TOTP_ISSUER, StandardCharsets.UTF_8);

        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data="
                + URLEncoder.encode(otpauthUrl, StandardCharsets.UTF_8);

        return ResponseEntity.ok(
                TwoFactorQrResponse.builder()
                        .perfilUsuario(perfilUsuario)
                        .secreto2fa(secret)
                        .otpauthUrl(otpauthUrl)
                        .qrCodeUrl(qrCodeUrl)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {
        Usuario updated = usuarioService.update(id, toEntity(usuarioDto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioDto toDto(Usuario usuario) {
        return UsuarioDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .perfilUsuario(usuario.getPerfilUsuario())
                // Nunca exponer la contraseña real al cliente.
                .contrasenaUsuario(null)
                .build();
    }

    private Usuario toEntity(UsuarioDto dto) {
        return Usuario.builder()
                .idUsuario(dto.getIdUsuario())
                .perfilUsuario(dto.getPerfilUsuario())
                .contrasenaUsuario(dto.getContrasenaUsuario())
                .build();
    }

    private String generateBase32Secret(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder secret = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            secret.append(BASE32_ALPHABET[random.nextInt(BASE32_ALPHABET.length)]);
        }
        return secret.toString();
    }
}
