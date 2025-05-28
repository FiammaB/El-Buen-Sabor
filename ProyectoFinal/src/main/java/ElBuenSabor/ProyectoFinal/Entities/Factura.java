package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Table(name = "factura") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// Para Factura, el borrado lógico podría significar 'anulada'.
// Si una factura se anula, se genera una nota de crédito.
// La factura original podría marcarse como 'baja = true' o tener un estado específico.
// Usaremos 'baja' para la anulación por simplicidad con el patrón actual.
@SQLDelete(sql = "UPDATE factura SET baja = true WHERE id = ?") // Anular factura
@Where(clause = "baja = false") // Mostrar solo facturas no anuladas por defecto
public class Factura extends BaseEntity {

    private LocalDate fechaFacturacion;
    private Integer mpPaymentId;        // ID de pago de Mercado Pago
    private Integer mpMerchantOrderId;  // ID de la orden de Mercado Pago
    private String mpPreferenceId;      // ID de preferencia de Mercado Pago
    private String mpPaymentType;       // Tipo de pago de Mercado Pago

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;        // Forma de pago registrada en la factura

    private Double totalVenta;          // Total de la venta en la factura

    // La relación OneToOne con Pedido se mapea desde Pedido.
    // Si necesitamos navegar de Factura a Pedido, podríamos añadir:
    // @OneToOne
    // @JoinColumn(name = "pedido_id") // Asumiendo que la FK está en la tabla Factura
    // private Pedido pedido;
    // Sin embargo, tu entidad Pedido ya tiene:
    // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "factura_id", referencedColumnName = "id")
    // private Factura factura;
    // Esto significa que la FK 'factura_id' está en la tabla 'pedido'.
    // Para navegar de Factura a Pedido, necesitaríamos un @OneToOne(mappedBy="factura") en Pedido
    // O una query específica en el repositorio de Pedido.
    // Por ahora, no añadimos la relación aquí para evitar problemas de mapeo bidireccional si no es estrictamente necesario.

    // Considerar un campo para el ID de la Nota de Crédito si se genera una al anular.
    // private Long notaCreditoId;
}
