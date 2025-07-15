package ElBuenSabor.ProyectoFinal.DTO;


import lombok.*;
import java.time.LocalDate;
import java.util.List; // Si DetallePedidoDTO es una lista

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaCreditoDTO {
    private Long id;
    private LocalDate fechaEmision;
    private Double total;
    private String motivo;
    private Long facturaAnuladaId; // Solo el ID de la factura anulada
    private Long pedidoOriginalId; // Solo el ID del pedido original
    private PersonaDTO persona; // Datos del persona
    private List<DetallePedidoDTO> detalles; // Detalles copiados
    private String urlPdfNotaCredito; // URL del PDF de la Nota de Crédito
}
