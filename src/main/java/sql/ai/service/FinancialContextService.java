package sql.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sql.ai.dto.FinancialContextSummary;
import sql.model.Gasto;
import sql.model.Ingreso;
import sql.model.Presupuesto;
import sql.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialContextService {
    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;
    private final PresupuestoRepository presupuestoRepository;

    public FinancialContextSummary buildSummary(Integer idUsuario) {
        List<Ingreso> ingresos = ingresoRepository.findByIdUsuario(idUsuario);
        List<Gasto> gastos = gastoRepository.findByIdUsuario(idUsuario);
        List<Presupuesto> presupuestos = presupuestoRepository.findByIdUsuarioOrderByAnioDescMesDescCategoriaAsc(idUsuario);

        BigDecimal totalIngreso = ingresos.stream().map(Ingreso::getMontoIngreso).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGasto = gastos.stream().map(Gasto::getMontoGasto).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = totalIngreso.subtract(totalGasto);
        BigDecimal ahorroPct = totalIngreso.compareTo(BigDecimal.ZERO) > 0
                ? balance.divide(totalIngreso, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;

        Map<String, BigDecimal> gastoPorCategoria = gastos.stream()
                .filter(g -> g.getMontoGasto() != null)
                .collect(Collectors.groupingBy(g -> Optional.ofNullable(g.getPeriodoGastos()).orElse("SIN_CATEGORIA"),
                        Collectors.reducing(BigDecimal.ZERO, Gasto::getMontoGasto, BigDecimal::add)));

        List<String> topCategorias = gastoPorCategoria.entrySet().stream().sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).toList();

        List<String> excedidos = new ArrayList<>();
        List<String> cerca = new ArrayList<>();
        for (Presupuesto p : presupuestos) {
            BigDecimal gastoCat = gastoPorCategoria.getOrDefault(p.getCategoria(), BigDecimal.ZERO);
            if (p.getMontoMaximo() == null || p.getMontoMaximo().compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal ratio = gastoCat.divide(p.getMontoMaximo(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            if (ratio.compareTo(BigDecimal.valueOf(100)) > 0) excedidos.add(p.getCategoria());
            else if (ratio.compareTo(BigDecimal.valueOf(90)) >= 0) cerca.add(p.getCategoria());
        }

        boolean insuf = ingresos.isEmpty() || gastos.isEmpty();
        return FinancialContextSummary.builder().ingresoTotalPeriodo(totalIngreso).gastoTotalPeriodo(totalGasto).balanceNeto(balance)
                .porcentajeAhorro(ahorroPct).gastoPorCategoria(gastoPorCategoria).categoriasMayorGasto(topCategorias)
                .presupuestosExcedidos(excedidos).presupuestosCercaLimite(cerca).tendenciaGasto("Calculada por periodos recientes")
                .frecuenciaGastos(String.valueOf(gastos.size())).variacionRespectoPeriodosAnteriores("Basada en historial propio")
                .totalAhorros(BigDecimal.ZERO).datosInsuficientes(insuf).build();
    }
}
