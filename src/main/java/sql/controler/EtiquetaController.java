package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.dto.EtiquetaDto;
import sql.model.Etiqueta;
import sql.service.EtiquetaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/etiquetas")
@AllArgsConstructor
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @GetMapping
    public ResponseEntity<List<EtiquetaDto>> getAll(@RequestParam Integer idUsuario) {
        List<Etiqueta> etiquetas = etiquetaService.getAll(idUsuario);
        if (etiquetas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(etiquetas.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody EtiquetaDto dto) {
        try {
            Etiqueta saved = etiquetaService.save(toEntity(dto));
            return ResponseEntity.ok(toDto(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, @RequestParam Integer idUsuario) {
        boolean deleted = etiquetaService.delete(id, idUsuario);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private EtiquetaDto toDto(Etiqueta etiqueta) {
        return EtiquetaDto.builder()
                .idEtiqueta(etiqueta.getIdEtiqueta())
                .idUsuario(etiqueta.getIdUsuario())
                .nombre(etiqueta.getNombre())
                .slug(etiqueta.getSlug())
                .build();
    }

    private Etiqueta toEntity(EtiquetaDto dto) {
        return Etiqueta.builder()
                .idEtiqueta(dto.getIdEtiqueta())
                .idUsuario(dto.getIdUsuario())
                .nombre(dto.getNombre())
                .slug(dto.getSlug())
                .build();
    }
}
