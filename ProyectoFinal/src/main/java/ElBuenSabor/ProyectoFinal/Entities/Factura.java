package ElBuenSabor.ProyectoFinal.Entities;



import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @OneToOne(mappedBy = "facturaAnulada") // Relación inversa con RegistroAnulacion
    private RegistroAnulacion registroAnulacion; // <-- Nueva propiedad

    // Opcional: Campo para marcar la factura como anulada
    private boolean anulada = false; // <-- Nueva propiedad
    @OneToOne(mappedBy = "factura")
    private Pedido pedido;
    @Column(length = 500)
    private String urlPdf;
}