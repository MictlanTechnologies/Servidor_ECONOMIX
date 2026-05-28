package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sql.dto.PresupuestoDto;
import sql.service.PresupuestoService;

import java.util.List;

@RestController
@RequestMapping("/economix/api/presupuestos")
@AllArgsConstructor
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    @GetMapping
    public ResponseEntity<List<PresupuestoDto>> getAll() {
        List<PresupuestoDto> lista = presupuestoService.getAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(presupuestoService.getById(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PresupuestoDto>> getByUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(presupuestoService.getByUsuario(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/periodo")
    public ResponseEntity<List<PresupuestoDto>> getByUsuarioAndPeriodo(
            @PathVariable Integer idUsuario,
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        return ResponseEntity.ok(presupuestoService.getByUsuarioAndPeriodo(idUsuario, mes, anio));
    }

    @GetMapping("/usuario/{idUsuario}/categoria")
    public ResponseEntity<PresupuestoDto> getByUsuarioCategoriaPeriodo(
            @PathVariable Integer idUsuario,
            @RequestParam String categoria,
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        PresupuestoDto presupuesto = presupuestoService.getByUsuarioCategoriaPeriodo(idUsuario, categoria, mes, anio);
        if (presupuesto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(presupuesto);
    }

    @PostMapping
    public ResponseEntity<PresupuestoDto> save(@RequestBody PresupuestoDto dto) {
        return ResponseEntity.ok(presupuestoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresupuestoDto> update(@PathVariable Integer id, @RequestBody PresupuestoDto dto) {
        return ResponseEntity.ok(presupuestoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        presupuestoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
