package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDetalleDTO {

    private Long id;
    private Double cantidad;
    private ArticuloInsumoShortDTO articuloInsumo;
}
