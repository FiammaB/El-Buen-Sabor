package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private LocalDate fechaPedido;
    private String estado;
    private String tipoEnvio;
    private String formaPago;
    private String observaciones;
    private Double total;
    private LocalTime horaEstimadaFinalizacion;
    private  Double totalCosto;
    private PersonaDTO persona;
    private DomicilioDTO domicilio;
    private SucursalDTO sucursal;

    private UsuarioDTO empleado;
    private FacturaDTO factura;
    private List<DetallePedidoDTO> detalles;
}

