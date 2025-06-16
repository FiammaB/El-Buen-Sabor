package ElBuenSabor.ProyectoFinal.Entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "detalle_pedido")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DetallePedido extends BaseEntity {

    private Integer cantidad; // Cantidad del artículo en este pedido
    private Double subTotal; // Subtotal de este ítem (cantidad * precio_unitario) [cite: 108]

    @ManyToOne
    @JoinColumn(name = "articulo_manufacturado_id")
    private ArticuloManufacturado articuloManufacturado;

    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private ArticuloInsumo articuloInsumo;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;
}
