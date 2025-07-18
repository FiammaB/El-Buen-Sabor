package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promocion_detalle")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PromocionDetalle extends BaseEntity {

    private Integer cantidad; // Cantidad del artículo en la promoción

    @ManyToOne
    @JoinColumn(name = "articulo_manufacturado_id")
    private ArticuloManufacturado articuloManufacturado;

    @ManyToOne
    @JoinColumn(name = "promocion_id")
    private Promocion promocion;
}