package sql.service;

import sql.dto.CategoriaPresupuestoDto;
import sql.dto.IngresoDisponibleDto;
import sql.dto.PresupuestoResumenDto;
import sql.model.AsignacionPresupuesto;
import sql.model.Presupuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PresupuestoService {
    List<Presupuesto> getAll();
    Presupuesto getById(Integer id);
    Presupuesto save(Presupuesto presupuesto);
    void delete(Integer id);
    Presupuesto update(Integer id, Presupuesto presupuesto);

    CategoriaPresupuestoDto crearCategoria(Integer usuarioId, String nombre, String colorHex, String iconKey);
    CategoriaPresupuestoDto actualizarCategoria(Integer usuarioId, Integer categoriaId, String colorHex, String iconKey, String nombre);
    List<CategoriaPresupuestoDto> listarCategorias(Integer usuarioId);
    void eliminarCategoria(Integer usuarioId, Integer categoriaId);
    AsignacionPresupuesto asignar(Integer usuarioId, Integer ingresoId, Integer categoriaId, BigDecimal monto, LocalDate fecha);
    List<PresupuestoResumenDto> resumenMensual(Integer usuarioId, int mes, int anio);
    List<IngresoDisponibleDto> ingresosDisponibles(Integer usuarioId, int mes, int anio);
    void validarGastoEnCategoria(Integer usuarioId, Integer categoriaId, BigDecimal montoGasto, LocalDate fecha, Integer gastoExcluirId);
}
