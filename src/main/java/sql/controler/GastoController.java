package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.dto.GastoDto;
import sql.model.Gasto;
import sql.service.GastoService;

import java.util.List;

@RestController
@RequestMapping("/economix/api/gastos")
@AllArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    @GetMapping
    public ResponseEntity<List<GastoDto>> getAll() {
        List<GastoDto> items = gastoService.getAll().stream().map(this::toDto).toList();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoDto> getById(@PathVariable Integer id) {
        Gasto gasto = gastoService.getById(id);
        if (gasto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(gasto));
    }

    @PostMapping
    public ResponseEntity<GastoDto> save(@RequestBody GastoDto dto) {
        return ResponseEntity.ok(toDto(gastoService.save(toEntity(dto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoDto> update(@PathVariable Integer id, @RequestBody GastoDto dto) {
        Gasto updated = gastoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        gastoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private GastoDto toDto(Gasto gasto) {
        return GastoDto.builder()
                .idGastos(gasto.getIdGastos())
                .idUsuario(gasto.getIdUsuario())
                .descripcionGasto(gasto.getDescripcionGasto())
                .articuloGasto(gasto.getArticuloGasto())
                .montoGasto(gasto.getMontoGasto())
                .fechaGastos(gasto.getFechaGastos())
                .periodoGastos(gasto.getPeriodoGastos())
                .idCategoria(gasto.getIdCategoria())
                .idPresupuesto(gasto.getIdPresupuesto())
                .build();
    }

    private Gasto toEntity(GastoDto dto) {
        return Gasto.builder()
                .idGastos(dto.getIdGastos())
                .idUsuario(dto.getIdUsuario())
                .descripcionGasto(dto.getDescripcionGasto())
                .articuloGasto(dto.getArticuloGasto())
                .montoGasto(dto.getMontoGasto())
                .fechaGastos(dto.getFechaGastos())
                .periodoGastos(dto.getPeriodoGastos())
                .idCategoria(dto.getIdCategoria())
                .idPresupuesto(dto.getIdPresupuesto())
                .build();
    }
}
