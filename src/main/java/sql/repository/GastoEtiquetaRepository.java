package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sql.model.GastoEtiqueta;

import java.util.List;

@Repository
public interface GastoEtiquetaRepository extends JpaRepository<GastoEtiqueta, Integer> {
    List<GastoEtiqueta> findByIdGastos(Integer idGastos);
    
    @Query("SELECT e.nombre FROM GastoEtiqueta ge JOIN Etiqueta e ON ge.idEtiqueta = e.idEtiqueta WHERE ge.idGastos = :idGastos")
    List<String> findEtiquetasByIdGastos(@Param("idGastos") Integer idGastos);
    
    void deleteByIdGastos(Integer idGastos);
}
