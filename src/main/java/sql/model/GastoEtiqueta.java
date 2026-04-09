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
@Table(name = "tbl_gasto_etiqueta")
public class GastoEtiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idGastoEtiqueta")
    private Integer idGastoEtiqueta;

    @Column(name = "idGasto", nullable = false)
    private Integer idGasto;

    @Column(name = "idEtiqueta", nullable = false)
    private Integer idEtiqueta;
}
