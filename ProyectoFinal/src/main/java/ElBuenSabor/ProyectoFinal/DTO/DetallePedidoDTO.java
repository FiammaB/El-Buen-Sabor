package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {
    private Long id;
    private Integer cantidad;
    private Double subTotal;

    // For request, you'd send one of these IDs
    private Long articuloManufacturadoId;
    private Long articuloInsumoId;

    // For response, you might embed the DTOs
    private ArticuloManufacturadoDTO articuloManufacturado;
    private ArticuloInsumoDTO articuloInsumo;
}