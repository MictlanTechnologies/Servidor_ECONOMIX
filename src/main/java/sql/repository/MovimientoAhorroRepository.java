package sql.repository;

import sql.model.MovimientoAhorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoAhorroRepository extends JpaRepository<MovimientoAhorro, Integer> {
    List<MovimientoAhorro> findByIdUsuario(Integer idUsuario);
}
