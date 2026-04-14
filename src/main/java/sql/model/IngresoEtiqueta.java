package sql.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_ingreso_etiqueta", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"idIngresos", "idEtiqueta"})
})
public class IngresoEtiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "idIngresos", nullable = false)
    private Integer idIngresos;

    @Column(name = "idEtiqueta", nullable = false)
    private Integer idEtiqueta;

    @ManyToOne
    @JoinColumn(name = "idIngresos", referencedColumnName = "idIngresos", insertable = false, updatable = false)
    private Ingreso ingreso;

    @ManyToOne
    @JoinColumn(name = "idEtiqueta", referencedColumnName = "idEtiqueta", insertable = false, updatable = false)
    private Etiqueta etiqueta;
}
