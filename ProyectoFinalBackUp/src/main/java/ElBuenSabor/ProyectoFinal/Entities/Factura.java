package ElBuenSabor.ProyectoFinal.Entities;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "factura")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Factura extends BaseEntity {

    private LocalDate fechaFacturacion;
    private Integer mpPaymentId;        // ID de pago de Mercado Pago [cite: 251]
    private Integer mpMerchantOrderId;  // ID de la orden de Mercado Pago [cite: 251]
    private String mpPreferenceId;      // ID de preferencia de Mercado Pago [cite: 251]
    private String mpPaymentType;       // Tipo de pago de Mercado Pago [cite: 251]
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;        // Forma de pago registrada en la factura [cite: 251]
    private Double totalVenta;          // Total de la venta en la factura [cite: 251]

    // La relación OneToOne con Pedido se mapea desde Pedido para evitar ciclos y duplicidad
    // No necesitamos @OneToOne aquí porque ya está en Pedido.
}