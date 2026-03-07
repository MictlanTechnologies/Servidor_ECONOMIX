package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.CategoriaPresupuesto;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaPresupuestoRepository extends JpaRepository<CategoriaPresupuesto, Integer> {
    List<CategoriaPresupuesto> findByUsuarioId(Integer usuarioId);
    Optional<CategoriaPresupuesto> findByIdAndUsuarioId(Integer id, Integer usuarioId);
}
