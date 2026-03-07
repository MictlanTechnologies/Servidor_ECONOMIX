package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngresoDisponibleDto {
    private Integer ingresoId;
    private String descripcionIngreso;
    private LocalDate fechaIngreso;
    private BigDecimal total;
    private BigDecimal asignadoMes;
    private BigDecimal disponibleMes;
}
