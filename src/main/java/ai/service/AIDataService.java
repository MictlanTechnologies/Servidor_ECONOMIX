package ai.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sql.model.Ahorro;
import sql.model.CategoriaGasto;
import sql.model.Gasto;
import sql.model.Ingreso;
import sql.model.MovimientoAhorro;
import sql.model.Presupuesto;
import sql.repository.AhorroRepository;
import sql.repository.CategoriaGastoRepository;
import sql.repository.GastoRepository;
import sql.repository.IngresoRepository;
import sql.repository.MovimientoAhorroRepository;
import sql.repository.PresupuestoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AIDataService {

    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaGastoRepository categoriaGastoRepository;
    private final AhorroRepository ahorroRepository;
    private final MovimientoAhorroRepository movimientoAhorroRepository;

    public List<Gasto> getGastos(Integer userId, LocalDate from, LocalDate to, Integer categoryId) {
        validateDateRange(from, to);
        if (categoryId == null) {
            return gastoRepository.findByIdUsuarioAndFechaGastosBetween(userId, from, to);
        }
        return gastoRepository.findByIdUsuarioAndFechaGastosBetweenAndIdCategoria(userId, from, to, categoryId);
    }

    public List<Ingreso> getIngresos(Integer userId, LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        return ingresoRepository.findByIdUsuarioAndFechaIngresosBetween(userId, from, to);
    }

    public List<Presupuesto> getPresupuestos(Integer userId, Integer month, Integer year) {
        if (month == null || year == null) {
            throw new IllegalArgumentException("month y year son obligatorios");
        }
        return presupuestoRepository.findByIdUsuarioAndMesAndAnio(userId, month, year);
    }

    public List<CategoriaGasto> getCategorias(Integer userId) {
        return categoriaGastoRepository.findByIdUsuario(userId);
    }

    public Map<Integer, String> categoriaNombreMap(Integer userId) {
        return getCategorias(userId).stream()
                .collect(Collectors.toMap(CategoriaGasto::getIdCategoria, CategoriaGasto::getNombreCategoria, (a, b) -> a));
    }

    public List<Ahorro> getAhorros(Integer userId) {
        return ahorroRepository.findByIdUsuario(userId);
    }

    public List<MovimientoAhorro> getMovimientosAhorro(Integer userId) {
        return movimientoAhorroRepository.findByIdUsuario(userId);
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from y to son obligatorios");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Rango de fechas inválido: from no puede ser mayor a to");
        }
    }
}
