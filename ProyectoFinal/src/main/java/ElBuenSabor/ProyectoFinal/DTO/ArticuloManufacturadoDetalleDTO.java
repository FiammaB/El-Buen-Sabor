package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDetalleDTO extends BaseDTO{

    private Double cantidad;
    private ArticuloInsumoDTO articuloInsumo;
    //private ArticuloManufacturadoDTO articuloManufacturado;

}
