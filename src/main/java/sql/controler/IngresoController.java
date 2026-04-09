package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sql.dto.*;
import sql.model.*;
import sql.repository.EtiquetaRepository;
import sql.repository.IngresoEtiquetaRepository;
import sql.service.CategoriaService;
import sql.service.EtiquetaService;
import sql.service.IngresoService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/ingresos")
@AllArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;
    private final CategoriaService categoriaService;
    private final EtiquetaService etiquetaService;
    private final IngresoEtiquetaRepository ingresoEtiquetaRepository;
    private final EtiquetaRepository etiquetaRepository;

    @GetMapping
    public ResponseEntity<List<IngresoDto>> getAll() {
        List<Ingreso> ingresos = ingresoService.getAll();
        if (ingresos == null || ingresos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ingresos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoDto> getById(@PathVariable Integer id) {
        Ingreso ingreso = ingresoService.getById(id);
        if (ingreso == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(ingreso));
    }

    @PostMapping
    public ResponseEntity<IngresoDto> save(@RequestBody IngresoDto dto) {
        Ingreso ingreso = ingresoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(ingreso));
    }

    @PostMapping("/with-tags")
    @Transactional
    public ResponseEntity<?> saveWithTags(@RequestBody MovimientoConEtiquetasRequest<IngresoDto> request) {
        try {
            IngresoDto movimiento = request.getMovimiento();
            if (movimiento == null || movimiento.getIdUsuario() == null) {
                return ResponseEntity.badRequest().body("El movimiento e idUsuario son obligatorios");
            }

            Categoria categoriaResuelta = resolverCategoria(movimiento.getIdUsuario(), request.getCategoria(), TipoCategoria.INGRESO, movimiento.getIdCategoria());
            movimiento.setIdCategoria(categoriaResuelta.getIdCategoria());

            Ingreso ingresoGuardado = ingresoService.save(toEntity(movimiento));

            List<EtiquetaDto> etiquetas = new ArrayList<>();
            if (request.getEtiquetas() != null) {
                for (String rawEtiqueta : request.getEtiquetas()) {
                    Etiqueta etiqueta = etiquetaService.findOrCreate(movimiento.getIdUsuario(), rawEtiqueta);
                    ingresoEtiquetaRepository.save(IngresoEtiqueta.builder()
                            .idIngreso(ingresoGuardado.getIdIngresos())
                            .idEtiqueta(etiqueta.getIdEtiqueta())
                            .build());
                    etiquetas.add(EtiquetaDto.builder()
                            .idEtiqueta(etiqueta.getIdEtiqueta())
                            .idUsuario(etiqueta.getIdUsuario())
                            .nombre(etiqueta.getNombre())
                            .slug(etiqueta.getSlug())
                            .build());
                }
            }

            return ResponseEntity.ok(MovimientoConEtiquetasResponse.<IngresoDto>builder()
                    .movimiento(toDto(ingresoGuardado))
                    .categoria(toCategoriaDto(categoriaResuelta))
                    .etiquetas(etiquetas)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresoDto> update(@PathVariable Integer id, @RequestBody IngresoDto dto) {
        Ingreso updated = ingresoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ingresoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Categoria resolverCategoria(Integer idUsuario, CategoriaDto categoriaDto, TipoCategoria tipo, Integer idCategoriaMovimiento) {
        if (idCategoriaMovimiento != null) {
            Categoria categoria = categoriaService.findByIdAndUsuario(idCategoriaMovimiento, idUsuario);
            if (categoria == null) {
                throw new IllegalArgumentException("La categoría no pertenece al usuario");
            }
            if (categoria.getTipo() != tipo) {
                throw new IllegalArgumentException("El tipo de categoría no coincide con el movimiento");
            }
            return categoria;
        }

        if (categoriaDto == null || categoriaDto.getNombre() == null || categoriaDto.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe enviar una categoría válida");
        }

        return categoriaService.findOrCreate(
                idUsuario,
                tipo,
                categoriaDto.getNombre(),
                categoriaDto.getDescripcion(),
                categoriaDto.getColor()
        );
    }

    private CategoriaDto toCategoriaDto(Categoria categoria) {
        return CategoriaDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .idUsuario(categoria.getIdUsuario())
                .tipo(categoria.getTipo())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .color(categoria.getColor())
                .build();
    }

    private IngresoDto toDto(Ingreso ingreso) {
        List<String> etiquetas = ingresoEtiquetaRepository.findByIdIngreso(ingreso.getIdIngresos()).stream()
                .map(rel -> etiquetaRepository.findById(rel.getIdEtiqueta()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(Etiqueta::getNombre)
                .collect(Collectors.toList());

        return IngresoDto.builder()
                .idIngresos(ingreso.getIdIngresos())
                .idUsuario(ingreso.getIdUsuario())
                .montoIngreso(ingreso.getMontoIngreso())
                .periodicidadIngreso(ingreso.getPeriodicidadIngreso())
                .fechaIngresos(ingreso.getFechaIngresos())
                .descripcionIngreso(ingreso.getDescripcionIngreso())
                .idCategoria(ingreso.getIdCategoria())
                .etiquetas(etiquetas)
                .build();
    }

    private Ingreso toEntity(IngresoDto dto) {
        return Ingreso.builder()
                .idIngresos(dto.getIdIngresos())
                .idUsuario(dto.getIdUsuario())
                .montoIngreso(dto.getMontoIngreso())
                .periodicidadIngreso(dto.getPeriodicidadIngreso())
                .fechaIngresos(dto.getFechaIngresos())
                .descripcionIngreso(dto.getDescripcionIngreso())
                .idCategoria(dto.getIdCategoria())
                .build();
    }
}
