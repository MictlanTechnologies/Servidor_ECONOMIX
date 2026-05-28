package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Presupuesto;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {

    List<Presupuesto> findByIdUsuarioOrderByAnioDescMesDescCategoriaAsc(Integer idUsuario);

    List<Presupuesto> findByIdUsuarioAndMesAndAnioOrderByCategoriaAsc(Integer idUsuario, Integer mes, Integer anio);

    Optional<Presupuesto> findByIdPresupuestoAndIdUsuario(Integer idPresupuesto, Integer idUsuario);

    Optional<Presupuesto> findByIdUsuarioAndCategoriaIgnoreCaseAndMesAndAnio(
            Integer idUsuario,
            String categoria,
            Integer mes,
            Integer anio
    );

    boolean existsByIdUsuarioAndCategoriaIgnoreCaseAndMesAndAnio(
            Integer idUsuario,
            String categoria,
            Integer mes,
            Integer anio
    );
}
