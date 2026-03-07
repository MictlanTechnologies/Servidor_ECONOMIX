package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sql.dto.CategoriaPresupuestoDto;
import sql.dto.IngresoDisponibleDto;
import sql.dto.PresupuestoResumenDto;
import sql.model.AsignacionPresupuesto;
import sql.model.CategoriaPresupuesto;
import sql.model.Ingreso;
import sql.model.Presupuesto;
import sql.repository.AsignacionPresupuestoRepository;
import sql.repository.CategoriaPresupuestoRepository;
import sql.repository.GastoRepository;
import sql.repository.IngresoRepository;
import sql.repository.PresupuestoRepository;
import sql.service.PresupuestoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PresupuestoServiceImpl implements PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaPresupuestoRepository categoriaPresupuestoRepository;
    private final AsignacionPresupuestoRepository asignacionPresupuestoRepository;
    private final IngresoRepository ingresoRepository;
    private final GastoRepository gastoRepository;

    @Override
    public List<Presupuesto> getAll() {
        return presupuestoRepository.findAll();
    }

    @Override
    public Presupuesto getById(Integer id) {
        return presupuestoRepository.findById(id).orElse(null);
    }

    @Override
    public Presupuesto save(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }

    @Override
    public void delete(Integer id) {
        presupuestoRepository.deleteById(id);
    }

    @Override
    public Presupuesto update(Integer id, Presupuesto presupuesto) {
        return presupuestoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(presupuesto, existing, "idPresupuesto");
                    return presupuestoRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public CategoriaPresupuestoDto crearCategoria(Integer usuarioId, String nombre, String colorHex, String iconKey) {
        CategoriaPresupuesto categoria = categoriaPresupuestoRepository.save(CategoriaPresupuesto.builder()
                .usuarioId(usuarioId)
                .nombre(nombre)
                .colorHex(colorHex)
                .iconKey(iconKey)
                .createdAt(LocalDateTime.now())
                .build());
        return toCategoriaDto(categoria);
    }

    @Override
    public CategoriaPresupuestoDto actualizarCategoria(Integer usuarioId, Integer categoriaId, String colorHex, String iconKey, String nombre) {
        CategoriaPresupuesto categoria = categoriaPresupuestoRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada para el usuario."));

        if (nombre != null && !nombre.isBlank()) {
            categoria.setNombre(nombre);
        }
        categoria.setColorHex(colorHex);
        categoria.setIconKey(iconKey);

        return toCategoriaDto(categoriaPresupuestoRepository.save(categoria));
    }

    @Override
    public List<CategoriaPresupuestoDto> listarCategorias(Integer usuarioId) {
        return categoriaPresupuestoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toCategoriaDto)
                .toList();
    }

    @Override
    public void eliminarCategoria(Integer usuarioId, Integer categoriaId) {
        CategoriaPresupuesto categoria = categoriaPresupuestoRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada para el usuario."));

        boolean conAsignaciones = asignacionPresupuestoRepository.existsByCategoriaIdAndUsuarioId(categoriaId, usuarioId);
        boolean conGastos = gastoRepository.existsByIdCategoriaPresupuestoAndIdUsuario(categoriaId, usuarioId);
        if (conAsignaciones || conGastos) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la categoría porque tiene asignaciones o gastos relacionados.");
        }

        categoriaPresupuestoRepository.delete(categoria);
    }

    @Override
    public AsignacionPresupuesto asignar(Integer usuarioId, Integer ingresoId, Integer categoriaId, BigDecimal monto, LocalDate fecha) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto de asignación debe ser mayor que cero.");
        }

        Ingreso ingreso = ingresoRepository.findByIdIngresosAndIdUsuario(ingresoId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingreso no encontrado para el usuario."));

        categoriaPresupuestoRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada para el usuario."));

        int mes = fecha.getMonthValue();
        int anio = fecha.getYear();
        BigDecimal asignadoMes = asignacionPresupuestoRepository.sumAsignadoPorIngresoMes(usuarioId, ingresoId, mes, anio);
        BigDecimal disponibleIngreso = ingreso.getMontoIngreso().subtract(asignadoMes);

        if (monto.compareTo(disponibleIngreso) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Asignación excede el ingreso disponible. Disponible: $" + disponibleIngreso);
        }

        return asignacionPresupuestoRepository.save(AsignacionPresupuesto.builder()
                .usuarioId(usuarioId)
                .ingresoId(ingresoId)
                .categoriaId(categoriaId)
                .monto(monto)
                .fecha(fecha)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public List<PresupuestoResumenDto> resumenMensual(Integer usuarioId, int mes, int anio) {
        return categoriaPresupuestoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(cat -> {
                    BigDecimal asignado = asignacionPresupuestoRepository.sumAsignadoPorCategoriaMes(usuarioId, cat.getId(), mes, anio);
                    BigDecimal gastado = gastoRepository.sumGastosPorCategoriaMes(usuarioId, cat.getId(), mes, anio);
                    return PresupuestoResumenDto.builder()
                            .categoriaId(cat.getId())
                            .nombreCategoria(cat.getNombre())
                            .colorHex(cat.getColorHex())
                            .iconKey(cat.getIconKey())
                            .asignado(asignado)
                            .gastado(gastado)
                            .disponible(asignado.subtract(gastado))
                            .build();
                })
                .toList();
    }

    @Override
    public List<IngresoDisponibleDto> ingresosDisponibles(Integer usuarioId, int mes, int anio) {
        return ingresoRepository.findByIdUsuario(usuarioId)
                .stream()
                .map(ingreso -> {
                    BigDecimal asignado = asignacionPresupuestoRepository.sumAsignadoPorIngresoMes(usuarioId,
                            ingreso.getIdIngresos(), mes, anio);
                    BigDecimal total = ingreso.getMontoIngreso() == null ? BigDecimal.ZERO : ingreso.getMontoIngreso();
                    return IngresoDisponibleDto.builder()
                            .ingresoId(ingreso.getIdIngresos())
                            .descripcionIngreso(ingreso.getDescripcionIngreso())
                            .fechaIngreso(ingreso.getFechaIngresos())
                            .total(total)
                            .asignadoMes(asignado)
                            .disponibleMes(total.subtract(asignado))
                            .build();
                })
                .toList();
    }

    @Override
    public void validarGastoEnCategoria(Integer usuarioId, Integer categoriaId, BigDecimal montoGasto, LocalDate fecha, Integer gastoExcluirId) {
        if (categoriaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoriaId es obligatorio para registrar gastos.");
        }
        if (montoGasto == null || montoGasto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto del gasto debe ser mayor que cero.");
        }

        CategoriaPresupuesto categoria = categoriaPresupuestoRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La categoría de presupuesto no existe para el usuario."));

        int mes = fecha.getMonthValue();
        int anio = fecha.getYear();

        BigDecimal asignado = asignacionPresupuestoRepository.sumAsignadoPorCategoriaMes(usuarioId, categoriaId, mes, anio);
        BigDecimal gastado = gastoRepository.sumGastosPorCategoriaMesExcluyendoId(usuarioId, categoriaId, mes, anio, gastoExcluirId);
        BigDecimal disponible = asignado.subtract(gastado);

        if (montoGasto.compareTo(disponible) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Presupuesto insuficiente en la categoría " + categoria.getNombre() + ". Disponible: $" + disponible);
        }
    }

    private CategoriaPresupuestoDto toCategoriaDto(CategoriaPresupuesto categoria) {
        return CategoriaPresupuestoDto.builder()
                .id(categoria.getId())
                .usuarioId(categoria.getUsuarioId())
                .nombre(categoria.getNombre())
                .colorHex(categoria.getColorHex())
                .iconKey(categoria.getIconKey())
                .build();
    }
}
