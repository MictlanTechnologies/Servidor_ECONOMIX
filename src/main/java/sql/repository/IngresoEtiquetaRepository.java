package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.IngresoEtiqueta;

import java.util.List;

@Repository
public interface IngresoEtiquetaRepository extends JpaRepository<IngresoEtiqueta, Integer> {
    List<IngresoEtiqueta> findByIdIngreso(Integer idIngreso);
}
