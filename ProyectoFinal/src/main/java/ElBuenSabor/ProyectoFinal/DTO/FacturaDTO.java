package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.FormaPago; // Importar Enum
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Long id;

    @NotNull(message = "La fecha de facturación es obligatoria")
    @PastOrPresent(message = "La fecha de facturación no puede ser futura")
    private LocalDate fechaFacturacion;

    private Integer mpPaymentId;
    private Integer mpMerchantOrderId;
    private String mpPreferenceId;
    private String mpPaymentType;

    @NotNull(message = "La forma de pago es obligatoria")
    private FormaPago formaPago;

    @NotNull(message = "El total de venta es obligatorio")
    @Positive(message = "El total de venta debe ser positivo")
    private Double totalVenta;

    private boolean baja; // true si la factura está anulada

    // Opcional: Para mostrar a qué pedido pertenece esta factura
    private Long pedidoId;
    // private PedidoSimpleResponseDTO pedido; // Si se quisiera más detalle del pedido
}
