package sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sql.model.Gasto;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {
    List<Gasto> findByIdUsuario(Integer idUsuario);

    @Query(value = """
            SELECT COALESCE(SUM(montoGasto), 0)
            FROM tbl_gastos
            WHERE idUsuario = :idUsuario
              AND LOWER(TRIM(periodoGastos)) = LOWER(TRIM(:categoria))
              AND MONTH(fechaGastos) = :mes
              AND YEAR(fechaGastos) = :anio
            """, nativeQuery = true)
    BigDecimal sumMontoByUsuarioCategoriaMesAnio(
            @Param("idUsuario") Integer idUsuario,
            @Param("categoria") String categoria,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio
    );
}
