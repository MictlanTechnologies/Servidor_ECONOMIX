package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sql.model.Gasto;

import java.util.Optional;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {
    Optional<Gasto> findByIdGastosAndIdUsuario(Integer idGastos, Integer idUsuario);
}
