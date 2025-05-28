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
// No necesita @SQLDelete ni @Where propios si su ciclo de vida está totalmente ligado a Pedido
// y Pedido usa borrado lógico. Si un Pedido es baja=true, sus detalles son implícitamente "de baja".
public class DetallePedido extends BaseEntity { // Hereda 'id' y 'baja'

    @Column(nullable = false)
    private Integer cantidad; // Cantidad del artículo en este pedido

    @Column(nullable = false)
    private Double subTotal; // Subtotal de este ítem (cantidad * precio_unitario)

    // Relación con ArticuloManufacturado (productos finales)
    // Un detalle puede ser de un manufacturado O de un insumo.
    @ManyToOne(fetch = FetchType.EAGER) // Cargar el artículo es útil
    @JoinColumn(name = "articulo_manufacturado_id")
    private ArticuloManufacturado articuloManufacturado;

    // Relación con ArticuloInsumo (si el pedido contiene solo insumos/bebidas)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "articulo_insumo_id")
    private ArticuloInsumo articuloInsumo;

    // Relación con Pedido
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Un detalle DEBE pertenecer a un pedido
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido; // El pedido al que pertenece este detalle

    // Constraint para asegurar que solo uno de articuloManufacturado o articuloInsumo esté seteado
    // Esto se puede manejar a nivel de lógica de negocio en el servicio o con un @CheckConstraint a nivel de BD.
    // @Column(name = "articulo_id") // Si se quisiera una FK genérica a la tabla Articulo (menos común con JOINED)
    // private Long articuloId;
    // @Column(name = "tipo_articulo") // Para saber si es Insumo o Manufacturado
    // private String tipoArticulo;
}
