package sql.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import sql.dto.PresupuestoResumenDto;
import sql.model.CategoriaPresupuesto;
import sql.model.Ingreso;
import sql.repository.AsignacionPresupuestoRepository;
import sql.repository.CategoriaPresupuestoRepository;
import sql.repository.GastoRepository;
import sql.repository.IngresoRepository;
import sql.repository.PresupuestoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoServiceImplTest {

    @Mock
    private PresupuestoRepository presupuestoRepository;
    @Mock
    private CategoriaPresupuestoRepository categoriaPresupuestoRepository;
    @Mock
    private AsignacionPresupuestoRepository asignacionPresupuestoRepository;
    @Mock
    private IngresoRepository ingresoRepository;
    @Mock
    private GastoRepository gastoRepository;

    @InjectMocks
    private PresupuestoServiceImpl presupuestoService;

    private Ingreso ingreso;
    private CategoriaPresupuesto categoria;

    @BeforeEach
    void setUp() {
        ingreso = Ingreso.builder()
                .idIngresos(10)
                .idUsuario(1)
                .montoIngreso(new BigDecimal("1000.00"))
                .build();

        categoria = CategoriaPresupuesto.builder()
                .id(20)
                .usuarioId(1)
                .nombre("Comida")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void asignacionQueExcedeIngresoDisponible_debeFallar() {
        when(ingresoRepository.findByIdIngresosAndIdUsuario(10, 1)).thenReturn(Optional.of(ingreso));
        when(categoriaPresupuestoRepository.findByIdAndUsuarioId(20, 1)).thenReturn(Optional.of(categoria));
        when(asignacionPresupuestoRepository.sumAsignadoPorIngresoMes(1, 10, 5, 2025))
                .thenReturn(new BigDecimal("900.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                presupuestoService.asignar(1, 10, 20, new BigDecimal("200.00"), LocalDate.of(2025, 5, 10))
        );

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Asignación excede el ingreso disponible. Disponible: $100.00", ex.getReason());
    }

    @Test
    void gastoQueExcedeDisponibleCategoria_debeFallar() {
        when(categoriaPresupuestoRepository.findByIdAndUsuarioId(20, 1)).thenReturn(Optional.of(categoria));
        when(asignacionPresupuestoRepository.sumAsignadoPorCategoriaMes(1, 20, 5, 2025))
                .thenReturn(new BigDecimal("300.00"));
        when(gastoRepository.sumGastosPorCategoriaMesExcluyendoId(1, 20, 5, 2025, null))
                .thenReturn(new BigDecimal("280.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                presupuestoService.validarGastoEnCategoria(1, 20, new BigDecimal("30.00"), LocalDate.of(2025, 5, 11), null)
        );

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Presupuesto insuficiente en la categoría Comida. Disponible: $20.00", ex.getReason());
    }

    @Test
    void resumenMensualConsistente() {
        when(categoriaPresupuestoRepository.findByUsuarioId(1)).thenReturn(List.of(categoria));
        when(asignacionPresupuestoRepository.sumAsignadoPorCategoriaMes(1, 20, 5, 2025))
                .thenReturn(new BigDecimal("500.00"));
        when(gastoRepository.sumGastosPorCategoriaMes(1, 20, 5, 2025))
                .thenReturn(new BigDecimal("125.00"));

        List<PresupuestoResumenDto> resumen = presupuestoService.resumenMensual(1, 5, 2025);

        assertEquals(1, resumen.size());
        assertEquals(new BigDecimal("500.00"), resumen.get(0).getAsignado());
        assertEquals(new BigDecimal("125.00"), resumen.get(0).getGastado());
        assertEquals(new BigDecimal("375.00"), resumen.get(0).getDisponible());
    }
}
