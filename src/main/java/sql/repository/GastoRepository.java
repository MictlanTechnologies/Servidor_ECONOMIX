package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Gasto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {

    List<Gasto> findByIdUsuarioAndFechaGastosBetween(Integer idUsuario, LocalDate from, LocalDate to);

    List<Gasto> findByIdUsuarioAndFechaGastosBetweenAndIdCategoria(Integer idUsuario, LocalDate from, LocalDate to, Integer idCategoria);
}
