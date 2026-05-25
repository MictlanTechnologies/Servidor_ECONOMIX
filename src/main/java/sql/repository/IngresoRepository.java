package sql.repository;

import sql.model.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Integer> {
    List<Ingreso> findByIdUsuario(Integer idUsuario);
}
