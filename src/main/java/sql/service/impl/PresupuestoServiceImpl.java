package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sql.dto.PresupuestoDto;
import sql.model.Presupuesto;
import sql.repository.GastoRepository;
import sql.repository.PresupuestoRepository;
import sql.repository.UsuarioRepository;
import sql.service.PresupuestoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@AllArgsConstructor
public class PresupuestoServiceImpl implements PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<PresupuestoDto> getAll() {
        return presupuestoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public PresupuestoDto getById(Integer id) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado."));
        return toDto(presupuesto);
    }

    @Override
    public List<PresupuestoDto> getByUsuario(Integer idUsuario) {
        return presupuestoRepository.findByIdUsuarioOrderByAnioDescMesDescCategoriaAsc(idUsuario)
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<PresupuestoDto> getByUsuarioAndPeriodo(Integer idUsuario, Integer mes, Integer anio) {
        return presupuestoRepository.findByIdUsuarioAndMesAndAnioOrderByCategoriaAsc(idUsuario, mes, anio)
                .stream().map(this::toDto).toList();
    }

    @Override
    public PresupuestoDto getByUsuarioCategoriaPeriodo(Integer idUsuario, String categoria, Integer mes, Integer anio) {
        String categoriaNormalizada = normalizeCategoria(categoria);
        return presupuestoRepository.findByIdUsuarioAndCategoriaIgnoreCaseAndMesAndAnio(idUsuario, categoriaNormalizada, mes, anio)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public PresupuestoDto save(PresupuestoDto dto) {
        Integer idUsuario = requireValidIdUsuario(dto.getIdUsuario());
        String categoria = normalizeCategoria(dto.getCategoria());
        BigDecimal montoMaximo = requireValidMontoMaximo(dto.getMontoMaximo());
        Integer mes = requireValidMes(dto.getMes());
        Integer anio = requireValidAnio(dto.getAnio());

        if (presupuestoRepository.existsByIdUsuarioAndCategoriaIgnoreCaseAndMesAndAnio(idUsuario, categoria, mes, anio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un presupuesto para esa categoría, mes y año.");
        }

        Presupuesto saved = presupuestoRepository.save(Presupuesto.builder()
                .idUsuario(idUsuario)
                .categoria(categoria)
                .montoMaximo(montoMaximo)
                .mes(mes)
                .anio(anio)
                .build());

        return toDto(saved);
    }

    @Override
    public PresupuestoDto update(Integer id, PresupuestoDto dto) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado."));

        Integer idUsuario = requireValidIdUsuario(dto.getIdUsuario());
        String categoria = normalizeCategoria(dto.getCategoria());
        BigDecimal montoMaximo = requireValidMontoMaximo(dto.getMontoMaximo());
        Integer mes = requireValidMes(dto.getMes());
        Integer anio = requireValidAnio(dto.getAnio());

        presupuestoRepository.findByIdUsuarioAndCategoriaIgnoreCaseAndMesAndAnio(idUsuario, categoria, mes, anio)
                .ifPresent(existing -> {
                    if (!existing.getIdPresupuesto().equals(id)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un presupuesto para esa categoría, mes y año.");
                    }
                });

        presupuesto.setIdUsuario(idUsuario);
        presupuesto.setCategoria(categoria);
        presupuesto.setMontoMaximo(montoMaximo);
        presupuesto.setMes(mes);
        presupuesto.setAnio(anio);

        return toDto(presupuestoRepository.save(presupuesto));
    }

    @Override
    public void delete(Integer id) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado."));
        presupuestoRepository.delete(presupuesto);
    }

    private PresupuestoDto toDto(Presupuesto presupuesto) {
        BigDecimal montoGastado = gastoRepository.sumMontoByUsuarioCategoriaMesAnio(
                presupuesto.getIdUsuario(),
                presupuesto.getCategoria(),
                presupuesto.getMes(),
                presupuesto.getAnio()
        );

        if (montoGastado == null) {
            montoGastado = BigDecimal.ZERO;
        }

        BigDecimal montoRestante = presupuesto.getMontoMaximo().subtract(montoGastado);
        BigDecimal porcentajeUso = presupuesto.getMontoMaximo().compareTo(BigDecimal.ZERO) > 0
                ? montoGastado.multiply(BigDecimal.valueOf(100))
                .divide(presupuesto.getMontoMaximo(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String estado;
        if (porcentajeUso.compareTo(BigDecimal.valueOf(100)) >= 0) {
            estado = "EXCEDIDO";
        } else if (porcentajeUso.compareTo(BigDecimal.valueOf(80)) >= 0) {
            estado = "ADVERTENCIA";
        } else {
            estado = "NORMAL";
        }

        return PresupuestoDto.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .idUsuario(presupuesto.getIdUsuario())
                .idCategoria(null)
                .categoria(presupuesto.getCategoria())
                .montoMaximo(presupuesto.getMontoMaximo())
                .montoGastado(montoGastado)
                .montoRestante(montoRestante)
                .porcentajeUso(porcentajeUso)
                .estado(estado)
                .mes(presupuesto.getMes())
                .anio(presupuesto.getAnio())
                .build();
    }

    private Integer requireValidIdUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idUsuario es obligatorio.");
        }
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
        return idUsuario;
    }

    private String normalizeCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría es obligatoria.");
        }
        return categoria.trim();
    }

    private BigDecimal requireValidMontoMaximo(BigDecimal montoMaximo) {
        if (montoMaximo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto máximo es obligatorio.");
        }
        if (montoMaximo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto máximo debe ser mayor que 0.");
        }
        return montoMaximo;
    }

    private Integer requireValidMes(Integer mes) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes debe estar entre 1 y 12.");
        }
        return mes;
    }

    private Integer requireValidAnio(Integer anio) {
        if (anio == null || anio < 2000 || anio > 2100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año debe estar entre 2000 y 2100.");
        }
        return anio;
    }
}
