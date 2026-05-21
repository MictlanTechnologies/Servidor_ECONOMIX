package sql.controler;

import sql.dto.LoginRequest;
import sql.dto.UsuarioDto;
import sql.model.Usuario;
import sql.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginRequest request) {
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

        String stored = usuario.getContrasenaUsuario();
        boolean ok = request.getContrasenaUsuario().equals(stored)
                || (stored != null && stored.startsWith("$2") && passwordEncoder.matches(request.getContrasenaUsuario(), stored));
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(toDto(usuario));
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
                .contrasenaUsuario(usuario.getContrasenaUsuario())
                .build();
    }

    private Usuario toEntity(UsuarioDto dto) {
        return Usuario.builder()
                .idUsuario(dto.getIdUsuario())
                .perfilUsuario(dto.getPerfilUsuario())
                .contrasenaUsuario(dto.getContrasenaUsuario())
                .build();
    }
}
