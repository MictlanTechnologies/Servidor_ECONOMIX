package sql.service.impl;

import sql.model.Usuario;
import sql.repository.UsuarioRepository;
import sql.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario getById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario findByPerfilUsuario(String perfilUsuario) {
        return usuarioRepository.findByPerfilUsuario(perfilUsuario).orElse(null);
    }

    @Override
    public boolean existsByPerfilUsuario(String perfilUsuario) {
        return usuarioRepository.existsByPerfilUsuario(perfilUsuario);
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getContrasenaUsuario() != null && !usuario.getContrasenaUsuario().startsWith("$2")) {
            usuario.setContrasenaUsuario(passwordEncoder.encode(usuario.getContrasenaUsuario()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void delete(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario update(Integer id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(usuario, existing, "idUsuario");
                    if (existing.getContrasenaUsuario() != null && !existing.getContrasenaUsuario().startsWith("$2")) {
                        existing.setContrasenaUsuario(passwordEncoder.encode(existing.getContrasenaUsuario()));
                    }
                    return usuarioRepository.save(existing);
                })
                .orElse(null);
    }
}
