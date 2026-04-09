package sql.controler;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sql.dto.*;
import sql.model.*;
import sql.repository.EtiquetaRepository;
import sql.repository.GastoEtiquetaRepository;
import sql.service.CategoriaService;
import sql.service.EtiquetaService;
import sql.service.GastoService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/gastos")
@AllArgsConstructor
public class GastoController {

    private final GastoService gastoService;
    private final CategoriaService categoriaService;
    private final EtiquetaService etiquetaService;
    private final GastoEtiquetaRepository gastoEtiquetaRepository;
    private final EtiquetaRepository etiquetaRepository;

    @GetMapping
    public ResponseEntity<List<GastoDto>> getAll() {
        List<Gasto> gastos = gastoService.getAll();
        if (gastos == null || gastos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(gastos.stream().map(this::toDto).collect(Collectors.toList()));
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
        Gasto gasto = gastoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(gasto));
    }

    @PostMapping("/with-tags")
    @Transactional
    public ResponseEntity<?> saveWithTags(@RequestBody MovimientoConEtiquetasRequest<GastoDto> request) {
        try {
            GastoDto movimiento = request.getMovimiento();
            if (movimiento == null || movimiento.getIdUsuario() == null) {
                return ResponseEntity.badRequest().body("El movimiento e idUsuario son obligatorios");
            }

            Categoria categoriaResuelta = resolverCategoria(movimiento.getIdUsuario(), request.getCategoria(), TipoCategoria.GASTO, movimiento.getIdCategoria());
            movimiento.setIdCategoria(categoriaResuelta.getIdCategoria());

            Gasto gastoGuardado = gastoService.save(toEntity(movimiento));

            List<EtiquetaDto> etiquetas = new ArrayList<>();
            if (request.getEtiquetas() != null) {
                for (String rawEtiqueta : request.getEtiquetas()) {
                    Etiqueta etiqueta = etiquetaService.findOrCreate(movimiento.getIdUsuario(), rawEtiqueta);
                    gastoEtiquetaRepository.save(GastoEtiqueta.builder()
                            .idGasto(gastoGuardado.getIdGastos())
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

            return ResponseEntity.ok(MovimientoConEtiquetasResponse.<GastoDto>builder()
                    .movimiento(toDto(gastoGuardado))
                    .categoria(toCategoriaDto(categoriaResuelta))
                    .etiquetas(etiquetas)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
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

    private GastoDto toDto(Gasto gasto) {
        List<String> etiquetas = gastoEtiquetaRepository.findByIdGasto(gasto.getIdGastos()).stream()
                .map(rel -> etiquetaRepository.findById(rel.getIdEtiqueta()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(Etiqueta::getNombre)
                .collect(Collectors.toList());

        return GastoDto.builder()
                .idGastos(gasto.getIdGastos())
                .idUsuario(gasto.getIdUsuario())
                .descripcionGasto(gasto.getDescripcionGasto())
                .articuloGasto(gasto.getArticuloGasto())
                .montoGasto(gasto.getMontoGasto())
                .fechaGastos(gasto.getFechaGastos())
                .periodoGastos(gasto.getPeriodoGastos())
                .idCategoria(gasto.getIdCategoria())
                .etiquetas(etiquetas)
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
                .build();
    }
}
