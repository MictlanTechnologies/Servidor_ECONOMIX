package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.CategoriaGasto;

import java.util.List;

@Repository
public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Integer> {

    List<CategoriaGasto> findByIdUsuario(Integer idUsuario);
}
