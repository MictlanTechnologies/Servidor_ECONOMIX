package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Presupuesto;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
    List<Presupuesto> findByIdUsuario(Integer idUsuario);
    Optional<Presupuesto> findByIdUsuarioAndIdCategoriaAndMesAndAnio(Integer idUsuario, Integer idCategoria, Integer mes, Integer anio);
    List<Presupuesto> findByIdUsuarioAndMesAndAnio(Integer idUsuario, Integer mes, Integer anio);
}
