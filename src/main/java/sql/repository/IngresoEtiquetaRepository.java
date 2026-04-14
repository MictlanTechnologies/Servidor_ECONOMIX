package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sql.model.IngresoEtiqueta;

import java.util.List;

@Repository
public interface IngresoEtiquetaRepository extends JpaRepository<IngresoEtiqueta, Integer> {
    List<IngresoEtiqueta> findByIdIngresos(Integer idIngresos);
    
    @Query("SELECT e.nombre FROM IngresoEtiqueta ie JOIN Etiqueta e ON ie.idEtiqueta = e.idEtiqueta WHERE ie.idIngresos = :idIngresos")
    List<String> findEtiquetasByIdIngresos(@Param("idIngresos") Integer idIngresos);
    
    void deleteByIdIngresos(Integer idIngresos);
}
