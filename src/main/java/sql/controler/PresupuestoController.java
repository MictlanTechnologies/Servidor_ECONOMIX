package sql.controler;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.dto.AsignacionRequestDto;
import sql.dto.CategoriaPresupuestoDto;
import sql.dto.CrearCategoriaRequest;
import sql.dto.IngresoDisponibleDto;
import sql.dto.PresupuestoDto;
import sql.dto.PresupuestoResumenDto;
import sql.dto.UpdateCategoriaRequest;
import sql.model.AsignacionPresupuesto;
import sql.model.Presupuesto;
import sql.service.PresupuestoService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/economix/api/presupuestos", "/api/presupuestos"})
@AllArgsConstructor
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    @GetMapping
    public ResponseEntity<List<PresupuestoDto>> getAll() {
        List<Presupuesto> presupuestos = presupuestoService.getAll();
        if (presupuestos == null || presupuestos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(presupuestos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDto> getById(@PathVariable Integer id) {
        Presupuesto presupuesto = presupuestoService.getById(id);
        if (presupuesto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(presupuesto));
    }

    @PostMapping
    public ResponseEntity<PresupuestoDto> save(@RequestBody PresupuestoDto dto) {
        Presupuesto presupuesto = presupuestoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(presupuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresupuestoDto> update(@PathVariable Integer id, @RequestBody PresupuestoDto dto) {
        Presupuesto updated = presupuestoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        presupuestoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaPresupuestoDto> crearCategoria(@Valid @RequestBody CrearCategoriaRequest request) {
        return ResponseEntity.ok(presupuestoService.crearCategoria(
                request.getUsuarioId(),
                request.getNombre(),
                request.getColorHex(),
                request.getIconKey()
        ));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaPresupuestoDto>> listarCategorias(@RequestParam Integer usuarioId) {
        List<CategoriaPresupuestoDto> categorias = presupuestoService.listarCategorias(usuarioId);
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaPresupuestoDto> actualizarCategoria(@PathVariable Integer id,
                                                                       @Valid @RequestBody UpdateCategoriaRequest request) {
        return ResponseEntity.ok(presupuestoService.actualizarCategoria(
                request.getUsuarioId(),
                id,
                request.getColorHex(),
                request.getIconKey(),
                request.getNombre()
        ));
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id,
                                                  @RequestParam Integer usuarioId) {
        presupuestoService.eliminarCategoria(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/asignaciones")
    public ResponseEntity<AsignacionPresupuesto> asignar(@Valid @RequestBody AsignacionRequestDto request) {
        return ResponseEntity.ok(presupuestoService.asignar(
                request.getUsuarioId(),
                request.getIngresoId(),
                request.getCategoriaId(),
                request.getMonto(),
                request.getFecha()
        ));
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<PresupuestoResumenDto>> resumenMensual(@RequestParam Integer usuarioId,
                                                                      @RequestParam(required = false) Integer mes,
                                                                      @RequestParam(required = false) Integer anio) {
        LocalDate ahora = LocalDate.now();
        int mesResuelto = mes != null ? mes : ahora.getMonthValue();
        int anioResuelto = anio != null ? anio : ahora.getYear();
        return ResponseEntity.ok(presupuestoService.resumenMensual(usuarioId, mesResuelto, anioResuelto));
    }

    @GetMapping("/ingresos-disponibles")
    public ResponseEntity<List<IngresoDisponibleDto>> ingresosDisponibles(@RequestParam Integer usuarioId,
                                                                          @RequestParam(required = false) Integer mes,
                                                                          @RequestParam(required = false) Integer anio) {
        LocalDate ahora = LocalDate.now();
        int mesResuelto = mes != null ? mes : ahora.getMonthValue();
        int anioResuelto = anio != null ? anio : ahora.getYear();
        return ResponseEntity.ok(presupuestoService.ingresosDisponibles(usuarioId, mesResuelto, anioResuelto));
    }

    private PresupuestoDto toDto(Presupuesto presupuesto) {
        return PresupuestoDto.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .idUsuario(presupuesto.getIdUsuario())
                .idCategoria(presupuesto.getIdCategoria())
                .categoria(presupuesto.getCategoria())
                .montoMaximo(presupuesto.getMontoMaximo())
                .montoGastado(presupuesto.getMontoGastado())
                .mes(presupuesto.getMes())
                .anio(presupuesto.getAnio())
                .build();
    }

    private Presupuesto toEntity(PresupuestoDto dto) {
        return Presupuesto.builder()
                .idPresupuesto(dto.getIdPresupuesto())
                .idUsuario(dto.getIdUsuario())
                .idCategoria(dto.getIdCategoria())
                .categoria(dto.getCategoria())
                .montoMaximo(dto.getMontoMaximo())
                .montoGastado(dto.getMontoGastado())
                .mes(dto.getMes())
                .anio(dto.getAnio())
                .build();
    }
}
