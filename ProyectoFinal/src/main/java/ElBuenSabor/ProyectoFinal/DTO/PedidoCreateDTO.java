package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoEnvio;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    // domicilioEntregaId es obligatorio si tipoEnvio es DELIVERY
    private Long domicilioEntregaId;

    @NotNull(message = "El tipo de envío es obligatorio")
    private TipoEnvio tipoEnvio;

    @NotNull(message = "La forma de pago es obligatoria")
    private FormaPago formaPago;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    @NotNull(message = "El pedido debe tener al menos un detalle")
    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    @Valid // Para validar los DetallePedidoCreateDTO anidados
    private Set<DetallePedidoCreateDTO> detallesPedidos = new HashSet<>();

    // Validaciones a nivel de clase (ej. domicilioId si es DELIVERY) se pueden hacer con @AssertTrue o en el servicio.
    // @AssertTrue(message = "El ID del domicilio de entrega es obligatorio para envíos a domicilio")
    // private boolean isDomicilioEntregaIdPresentForDelivery() {
    //     if (tipoEnvio == TipoEnvio.DELIVERY) {
    //         return domicilioEntregaId != null;
    //     }
    //     return true;
    // }
}
