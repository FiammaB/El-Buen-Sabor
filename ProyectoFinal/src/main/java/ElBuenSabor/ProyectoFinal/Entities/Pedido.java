package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedido")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE pedido SET baja = true WHERE id = ?") // Para borrado lógico (anulación)
@Where(clause = "baja = false") // Por defecto, solo se muestran pedidos no anulados/borrados
public class Pedido extends BaseEntity { // Hereda id y baja

    @Column(nullable = false)
    private LocalDate fechaPedido; // HU#6

    // HU#10, HU#192: Calculado por el sistema. El tipo DateTime en el diagrama de consignas sugiere LocalTime o LocalDateTime.
    // Usaremos LocalTime para la hora, asumiendo que la fecha ya está en fechaPedido.
    private LocalTime horaEstimadaFinalizacion;

    @Column(nullable = false)
    private Double total; // Total final después de descuentos. HU#6

    private Double totalCosto; // Costo total de los insumos del pedido. HU#25, HU#94

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado; // A_CONFIRMAR, EN_COCINA, LISTO, EN_DELIVERY, ENTREGADO, CANCELADO, RECHAZADO, FACTURADO. HU#11, HU#12, HU#13

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEnvio tipoEnvio; // DELIVERY, RETIRO_EN_LOCAL. HU#7

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago; // EFECTIVO, MERCADO_PAGO. HU#8

    private Double montoDescuento; // Para el 10% de descuento si retira en local. HU#7

    // Número de pedido podría ser el ID o un campo aparte si se requiere un formato específico.
    // Por ahora, el ID de la entidad BaseEntity sirve como número de pedido.

    // --- Relaciones ---
    @ManyToOne(fetch = FetchType.EAGER) // Un pedido pertenece a un Cliente. EAGER para tener datos del cliente.
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente; // HU#6

    @ManyToOne(fetch = FetchType.EAGER) // Domicilio de entrega, puede ser null si es retiro en local.
    @JoinColumn(name = "domicilio_id")
    private Domicilio domicilioEntrega; // HU#6

    @ManyToOne(fetch = FetchType.EAGER) // Un pedido se realiza en una Sucursal.
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    // Relación con Factura: Un pedido tiene una factura.
    // Pedido es el dueño de la relación (tiene la FK factura_id).
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "factura_id", referencedColumnName = "id")
    private Factura factura; // HU#12, HU#14

    // Relación con DetallePedido: Un pedido tiene muchos detalles.
    // CascadeType.ALL y orphanRemoval=true: Si se borra (físicamente) un pedido, se borran sus detalles.
    // Si el pedido se borra lógicamente, los detalles permanecen pero están asociados a un pedido 'baja=true'.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default // Para que Lombok Builder inicialice el Set
    private Set<DetallePedido> detallesPedidos = new HashSet<>(); // HU#6
}
