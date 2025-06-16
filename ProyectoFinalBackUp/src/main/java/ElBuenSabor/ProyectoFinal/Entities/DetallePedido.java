package ElBuenSabor.ProyectoFinal.Entities;


import jakarta.persistence.*;
import lombok.*;

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

    // Relación con ArticuloManufacturado (productos finales)
    @ManyToOne
    @JoinColumn(name = "articulo_manufacturado_id")
    private ArticuloManufacturado articuloManufacturado;

    // Relación con ArticuloInsumo (si el pedido contiene solo insumos/bebidas)
    @ManyToOne
    @JoinColumn(name = "articulo_insumo_id")
    private ArticuloInsumo articuloInsumo;

    // Relación con Pedido
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido; // El pedido al que pertenece este detalle
}
