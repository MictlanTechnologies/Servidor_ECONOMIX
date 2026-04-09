package sql.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_ingreso_etiqueta")
public class IngresoEtiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIngresoEtiqueta")
    private Integer idIngresoEtiqueta;

    @Column(name = "idIngreso", nullable = false)
    private Integer idIngreso;

    @Column(name = "idEtiqueta", nullable = false)
    private Integer idEtiqueta;
}
