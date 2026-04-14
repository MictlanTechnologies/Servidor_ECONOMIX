package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Etiqueta;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {
    List<Etiqueta> findByIdUsuario(Integer idUsuario);
    Optional<Etiqueta> findByIdUsuarioAndSlug(Integer idUsuario, String slug);
    Optional<Etiqueta> findByIdUsuarioAndNombre(Integer idUsuario, String nombre);
}
