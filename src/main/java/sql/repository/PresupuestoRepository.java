package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Presupuesto;

import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {

    List<Presupuesto> findByIdUsuarioAndMesAndAnio(Integer idUsuario, Integer mes, Integer anio);
}
