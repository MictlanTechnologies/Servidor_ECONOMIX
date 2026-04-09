package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Categoria;
import sql.model.TipoCategoria;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByIdUsuarioAndTipo(Integer idUsuario, TipoCategoria tipo);
    List<Categoria> findByIdUsuario(Integer idUsuario);
    Optional<Categoria> findByIdCategoriaAndIdUsuario(Integer idCategoria, Integer idUsuario);
    Optional<Categoria> findByIdUsuarioAndTipoAndNombreIgnoreCase(Integer idUsuario, TipoCategoria tipo, String nombre);
}
