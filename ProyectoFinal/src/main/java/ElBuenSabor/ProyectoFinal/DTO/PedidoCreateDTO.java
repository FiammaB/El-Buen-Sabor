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
    private TipoEnvio tipoEnvio;
    private FormaPago formaPago;
    private String observaciones;
    private Double total;

    private Long personaId;
    private Long domicilioId;
    private Long sucursalId;
    private Long empleadoId;
    private String telefono;
    private FacturaCreateDTO factura;
    private List<DetallePedidoCreateDTO> detalles;
}