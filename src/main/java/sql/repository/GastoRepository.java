package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sql.model.Gasto;

import java.math.BigDecimal;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {

    @Query("SELECT COALESCE(SUM(g.montoGasto), 0) FROM Gasto g " +
            "WHERE g.idUsuario = :usuarioId AND g.idCategoriaPresupuesto = :categoriaId " +
            "AND FUNCTION('MONTH', g.fechaGastos) = :mes AND FUNCTION('YEAR', g.fechaGastos) = :anio")
    BigDecimal sumGastosPorCategoriaMes(@Param("usuarioId") Integer usuarioId,
                                        @Param("categoriaId") Integer categoriaId,
                                        @Param("mes") int mes,
                                        @Param("anio") int anio);

    @Query("SELECT COALESCE(SUM(g.montoGasto), 0) FROM Gasto g " +
            "WHERE g.idUsuario = :usuarioId AND g.idCategoriaPresupuesto = :categoriaId " +
            "AND FUNCTION('MONTH', g.fechaGastos) = :mes AND FUNCTION('YEAR', g.fechaGastos) = :anio " +
            "AND (:excludeId IS NULL OR g.idGastos <> :excludeId)")
    BigDecimal sumGastosPorCategoriaMesExcluyendoId(@Param("usuarioId") Integer usuarioId,
                                                    @Param("categoriaId") Integer categoriaId,
                                                    @Param("mes") int mes,
                                                    @Param("anio") int anio,
                                                    @Param("excludeId") Integer excludeId);

    boolean existsByIdCategoriaPresupuestoAndIdUsuario(Integer idCategoriaPresupuesto, Integer idUsuario);
}
