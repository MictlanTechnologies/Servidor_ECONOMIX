package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.GastoEtiqueta;

import java.util.List;

@Repository
public interface GastoEtiquetaRepository extends JpaRepository<GastoEtiqueta, Integer> {
    List<GastoEtiqueta> findByIdGasto(Integer idGasto);
}
