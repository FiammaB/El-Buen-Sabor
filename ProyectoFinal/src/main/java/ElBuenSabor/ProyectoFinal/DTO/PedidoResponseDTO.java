package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.TipoEnvio;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    //Para ver un pedido
    private Long id;
    private LocalTime horaEstimadaFinalizacion;
    private Double total;
    private Double totalCosto;
    private Estado estado;
    private TipoEnvio tipoEnvio;
    private FormaPago formaPago;
    private LocalDate fechaPedido;
    private ClienteResponseDTO cliente; // Or a simpler ClienteSimpleDTO
    private DomicilioDTO domicilioEntrega;
    private FacturaDTO factura; // Nullable if not yet facturado
    private SucursalDTO sucursal; // Or SucursalSimpleDTO
    private Set<DetallePedidoDTO> detallesPedidos;
}