package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sql.model.AsignacionPresupuesto;

import java.math.BigDecimal;

@Repository
public interface AsignacionPresupuestoRepository extends JpaRepository<AsignacionPresupuesto, Integer> {

    @Query("SELECT COALESCE(SUM(a.monto), 0) FROM AsignacionPresupuesto a " +
            "WHERE a.usuarioId = :usuarioId AND a.ingresoId = :ingresoId " +
            "AND FUNCTION('MONTH', a.fecha) = :mes AND FUNCTION('YEAR', a.fecha) = :anio")
    BigDecimal sumAsignadoPorIngresoMes(@Param("usuarioId") Integer usuarioId,
                                        @Param("ingresoId") Integer ingresoId,
                                        @Param("mes") int mes,
                                        @Param("anio") int anio);

    @Query("SELECT COALESCE(SUM(a.monto), 0) FROM AsignacionPresupuesto a " +
            "WHERE a.usuarioId = :usuarioId AND a.categoriaId = :categoriaId " +
            "AND FUNCTION('MONTH', a.fecha) = :mes AND FUNCTION('YEAR', a.fecha) = :anio")
    BigDecimal sumAsignadoPorCategoriaMes(@Param("usuarioId") Integer usuarioId,
                                          @Param("categoriaId") Integer categoriaId,
                                          @Param("mes") int mes,
                                          @Param("anio") int anio);

    boolean existsByCategoriaIdAndUsuarioId(Integer categoriaId, Integer usuarioId);
}
