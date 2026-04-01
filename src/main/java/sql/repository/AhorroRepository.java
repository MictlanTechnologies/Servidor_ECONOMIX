package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Ahorro;

import java.util.List;

@Repository
public interface AhorroRepository extends JpaRepository<Ahorro, Integer> {

    List<Ahorro> findByIdUsuario(Integer idUsuario);
}
