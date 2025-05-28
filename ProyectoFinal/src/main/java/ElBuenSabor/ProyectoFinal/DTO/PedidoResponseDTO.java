package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.TipoEnvio;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoResponseDTO {
    private Long id; // Número de pedido
    private LocalDate fechaPedido;
    private LocalTime horaEstimadaFinalizacion;
    private Double total; // Total final pagado
    private Double totalCosto; // Costo interno
    private Double montoDescuento;
    private Estado estado;
    private TipoEnvio tipoEnvio;
    private FormaPago formaPago;
    private boolean baja; // Si el pedido está anulado

    private ClienteSimpleResponseDTO cliente; // Usar un DTO simple para el cliente
    private DomicilioDTO domicilioEntrega; // DTO completo del domicilio
    private SucursalSimpleDTO sucursal;   // DTO simple de la sucursal
    private FacturaDTO factura;           // DTO de la factura (puede ser null)

    private Set<DetallePedidoDTO> detallesPedidos = new HashSet<>();
}
