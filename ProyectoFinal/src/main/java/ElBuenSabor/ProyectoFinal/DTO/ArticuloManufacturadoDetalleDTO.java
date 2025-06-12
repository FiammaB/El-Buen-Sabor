package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDetalleDTO {
    private Long id;
    private Double cantidad;
    private Long articuloInsumoId;
    // For response, you might include more ArticuloInsumo details
    private ArticuloInsumoDTO articuloInsumo;
}