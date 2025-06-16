package ElBuenSabor.ProyectoFinal.DTO;


import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.TipoEnvio;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder; // Si quieres usar el patrón Builder para construirlo

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Útil para construir objetos PedidoDTO fácilmente
public class PedidoDTO {
    private Long id; // El ID del pedido, crucial para la respuesta
    private LocalTime horaEstimadaFinalizacion;
    private Double total;
    private Double totalCosto; // Opcional, si no lo quieres exponer al frontend
    private Estado estado;
    private TipoEnvio tipoEnvio;
    private FormaPago formaPago;
    private LocalDate fechaPedido;

    // Relaciones: se devuelven DTOs anidados o simplemente los IDs
    private Long clienteId; // O un ClienteDTO si necesitas más detalles del cliente
    private Long domicilioEntregaId; // O un DomicilioDTO
    private Long sucursalId; // O un SucursalDTO

    // Aquí, en lugar de DetallePedidoCreateDTO, podríamos tener un DetallePedidoDTO más completo
    private Set<DetallePedidoDTO> detallesPedidos; // Usar el DTO de detalle para la respuesta

    // No incluyas FacturaDTO aquí a menos que siempre la necesites.
    // Podría ser un endpoint separado para obtener la factura.
    // private FacturaDTO factura; // Si tuvieras un DTO para Factura
}