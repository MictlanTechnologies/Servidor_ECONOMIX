package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Ingreso;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Integer> {

    List<Ingreso> findByIdUsuarioAndFechaIngresosBetween(Integer idUsuario, LocalDate from, LocalDate to);
}
