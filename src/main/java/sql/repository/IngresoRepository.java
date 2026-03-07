package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Ingreso;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Integer> {
    Optional<Ingreso> findByIdIngresosAndIdUsuario(Integer idIngresos, Integer idUsuario);
    List<Ingreso> findByIdUsuario(Integer idUsuario);
}
