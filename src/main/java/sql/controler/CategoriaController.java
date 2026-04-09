package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sql.dto.CategoriaDto;
import sql.model.Categoria;
import sql.model.TipoCategoria;
import sql.service.CategoriaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/categorias")
@AllArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDto>> getAll(@RequestParam Integer idUsuario,
                                                      @RequestParam(required = false) TipoCategoria tipo) {
        List<Categoria> categorias = categoriaService.getAll(idUsuario, tipo);
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categorias.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CategoriaDto dto) {
        try {
            Categoria saved = categoriaService.save(toEntity(dto));
            return ResponseEntity.ok(toDto(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestParam Integer idUsuario,
                                    @RequestBody CategoriaDto dto) {
        try {
            Categoria updated = categoriaService.update(id, idUsuario, toEntity(dto));
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(toDto(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, @RequestParam Integer idUsuario) {
        boolean deleted = categoriaService.delete(id, idUsuario);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private CategoriaDto toDto(Categoria categoria) {
        return CategoriaDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .idUsuario(categoria.getIdUsuario())
                .tipo(categoria.getTipo())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .color(categoria.getColor())
                .build();
    }

    private Categoria toEntity(CategoriaDto dto) {
        return Categoria.builder()
                .idCategoria(dto.getIdCategoria())
                .idUsuario(dto.getIdUsuario())
                .tipo(dto.getTipo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .color(dto.getColor())
                .build();
    }
}
