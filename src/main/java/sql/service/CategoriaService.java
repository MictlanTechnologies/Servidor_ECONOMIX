package sql.service;

import sql.model.Categoria;
import sql.model.TipoCategoria;

import java.util.List;

public interface CategoriaService {
    List<Categoria> getAll(Integer idUsuario, TipoCategoria tipo);
    Categoria save(Categoria categoria);
    Categoria update(Integer idCategoria, Integer idUsuario, Categoria categoria);
    boolean delete(Integer idCategoria, Integer idUsuario);
    Categoria findByIdAndUsuario(Integer idCategoria, Integer idUsuario);
    Categoria findOrCreate(Integer idUsuario, TipoCategoria tipo, String nombre, String descripcion, String color);
}
