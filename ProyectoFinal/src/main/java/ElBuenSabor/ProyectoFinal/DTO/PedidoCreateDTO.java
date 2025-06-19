package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import ElBuenSabor.ProyectoFinal.Entities.TipoEnvio;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateDTO {

    private LocalDate fechaPedido;
    private String estado;
    private String tipoEnvio;
    private String formaPago;
    private String observaciones;
    private Double total;

    private Long clienteId;
    private Long domicilioId;
    private Long sucursalId; // opcional si se retira en sucursal
    private Long empleadoId; // puede venir nulo si aún no fue asignado

    private FacturaCreateDTO factura;
    private List<DetallePedidoCreateDTO> detalles;
}