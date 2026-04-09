package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sql.model.Categoria;
import sql.model.TipoCategoria;
import sql.repository.CategoriaRepository;
import sql.service.CategoriaService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> getAll(Integer idUsuario, TipoCategoria tipo) {
        if (idUsuario == null) {
            return List.of();
        }
        if (tipo != null) {
            return categoriaRepository.findByIdUsuarioAndTipo(idUsuario, tipo);
        }
        return categoriaRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public Categoria save(Categoria categoria) {
        String nombreNormalizado = categoria.getNombre() == null ? null : categoria.getNombre().trim();
        if (nombreNormalizado == null || nombreNormalizado.isBlank()) {
            throw new IllegalArgumentException("Nombre de categoría inválido");
        }
        categoriaRepository.findByIdUsuarioAndTipoAndNombreIgnoreCase(categoria.getIdUsuario(), categoria.getTipo(), nombreNormalizado)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
                });
        LocalDateTime now = LocalDateTime.now();
        categoria.setNombre(nombreNormalizado);
        categoria.setCreatedAt(now);
        categoria.setUpdatedAt(now);
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria update(Integer idCategoria, Integer idUsuario, Categoria categoria) {
        return categoriaRepository.findByIdCategoriaAndIdUsuario(idCategoria, idUsuario)
                .map(existing -> {
                    String nombreNormalizado = categoria.getNombre() == null ? null : categoria.getNombre().trim();
                    if (nombreNormalizado == null || nombreNormalizado.isBlank()) {
                        throw new IllegalArgumentException("Nombre de categoría inválido");
                    }
                    categoriaRepository.findByIdUsuarioAndTipoAndNombreIgnoreCase(idUsuario, categoria.getTipo(), nombreNormalizado)
                            .filter(dup -> !dup.getIdCategoria().equals(idCategoria))
                            .ifPresent(dup -> {
                                throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
                            });
                    existing.setTipo(categoria.getTipo());
                    existing.setNombre(nombreNormalizado);
                    existing.setDescripcion(categoria.getDescripcion());
                    existing.setColor(categoria.getColor());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return categoriaRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public boolean delete(Integer idCategoria, Integer idUsuario) {
        return categoriaRepository.findByIdCategoriaAndIdUsuario(idCategoria, idUsuario)
                .map(existing -> {
                    categoriaRepository.deleteById(existing.getIdCategoria());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Categoria findByIdAndUsuario(Integer idCategoria, Integer idUsuario) {
        return categoriaRepository.findByIdCategoriaAndIdUsuario(idCategoria, idUsuario).orElse(null);
    }

    @Override
    public Categoria findOrCreate(Integer idUsuario, TipoCategoria tipo, String nombre, String descripcion, String color) {
        String nombreNormalizado = nombre == null ? null : nombre.trim();
        if (nombreNormalizado == null || nombreNormalizado.isBlank()) {
            throw new IllegalArgumentException("Nombre de categoría inválido");
        }
        return categoriaRepository.findByIdUsuarioAndTipoAndNombreIgnoreCase(idUsuario, tipo, nombreNormalizado)
                .orElseGet(() -> save(Categoria.builder()
                        .idUsuario(idUsuario)
                        .tipo(tipo)
                        .nombre(nombreNormalizado)
                        .descripcion(descripcion)
                        .color(color)
                        .build()));
    }
}
