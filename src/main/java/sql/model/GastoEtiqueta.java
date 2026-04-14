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
@Table(name = "tbl_gasto_etiqueta", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"idGastos", "idEtiqueta"})
})
public class GastoEtiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "idGastos", nullable = false)
    private Integer idGastos;

    @Column(name = "idEtiqueta", nullable = false)
    private Integer idEtiqueta;

    @ManyToOne
    @JoinColumn(name = "idGastos", referencedColumnName = "idGastos", insertable = false, updatable = false)
    private Gasto gasto;

    @ManyToOne
    @JoinColumn(name = "idEtiqueta", referencedColumnName = "idEtiqueta", insertable = false, updatable = false)
    private Etiqueta etiqueta;
}
