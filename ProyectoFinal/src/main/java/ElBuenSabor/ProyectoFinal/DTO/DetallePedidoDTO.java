package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {

    private Long id;
    private Integer cantidad;
    private Double subTotal;

    private ArticuloManufacturadoDTO articuloManufacturado;
    private ArticuloInsumoShortDTO articuloInsumo;
}
