package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.dto.AhorroDto;
import sql.model.Ahorro;
import sql.service.AhorroService;

import java.util.List;

@RestController
@RequestMapping("/economix/api/ahorros")
@AllArgsConstructor
public class AhorroController {

    private final AhorroService ahorroService;

    @GetMapping
    public ResponseEntity<List<AhorroDto>> getAll() {
        List<AhorroDto> items = ahorroService.getAll().stream().map(this::toDto).toList();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhorroDto> getById(@PathVariable Integer id) {
        Ahorro ahorro = ahorroService.getById(id);
        if (ahorro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(ahorro));
    }

    @PostMapping
    public ResponseEntity<AhorroDto> save(@RequestBody AhorroDto ahorroDto) {
        return ResponseEntity.ok(toDto(ahorroService.save(toEntity(ahorroDto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhorroDto> update(@PathVariable Integer id, @RequestBody AhorroDto ahorroDto) {
        Ahorro updated = ahorroService.update(id, toEntity(ahorroDto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ahorroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AhorroDto toDto(Ahorro ahorro) {
        return AhorroDto.builder()
                .idAhorro(ahorro.getIdAhorro())
                .idUsuario(ahorro.getIdUsuario())
                .nombreObjetivo(ahorro.getNombreObjetivo())
                .descripcionObjetivo(ahorro.getDescripcionObjetivo())
                .meta(ahorro.getMeta())
                .montoAhorrado(ahorro.getMontoAhorrado())
                .fechaLimite(ahorro.getFechaLimite())
                .idIngresos(ahorro.getIdIngresos())
                .montoAhorro(ahorro.getMontoAhorro())
                .periodoTAhorro(ahorro.getPeriodoTAhorro())
                .fechaAhorro(ahorro.getFechaAhorro())
                .fechaActualizacionA(ahorro.getFechaActualizacionA())
                .build();
    }

    private Ahorro toEntity(AhorroDto dto) {
        return Ahorro.builder()
                .idAhorro(dto.getIdAhorro())
                .idUsuario(dto.getIdUsuario())
                .nombreObjetivo(dto.getNombreObjetivo())
                .descripcionObjetivo(dto.getDescripcionObjetivo())
                .meta(dto.getMeta())
                .montoAhorrado(dto.getMontoAhorrado())
                .fechaLimite(dto.getFechaLimite())
                .idIngresos(dto.getIdIngresos())
                .montoAhorro(dto.getMontoAhorro())
                .periodoTAhorro(dto.getPeriodoTAhorro())
                .fechaAhorro(dto.getFechaAhorro())
                .fechaActualizacionA(dto.getFechaActualizacionA())
                .build();
    }
}
